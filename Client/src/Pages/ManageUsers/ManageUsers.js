import React, {useEffect, useState} from "react";
import './ManageUsers.css'

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import Box from "@mui/material/Box";
import {
    Avatar,
    Collapse,
    Dialog,
    DialogTitle,
    Divider,
    Grid,
    IconButton, InputAdornment,
    List,
    ListItem,
    ListItemText, Stack, TextField
} from "@mui/material";
import Button from "@mui/material/Button";
import {useNavigate} from "react-router-dom";
import * as Space from 'react-spaces';
import Connection from "../../Communication/Connection";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";

/**
 * a function to return an object of the data the tables accepts
 * @param username username of the user
 * @param name the name of the user
 * @param role the role of the user
 * @param email the email of user
 * @param phoneNumber the phone number of the user
 * @param city the city of the user
 * @param schools the schools under the user
 * @returns {{role, phoneNumber, city, schools, name, email, username}} the object to return
 */
function createData(username, name, role, email, phoneNumber, city, schools) {
    return {
        username,
        name,
        role,
        email,
        phoneNumber,
        city,
        schools,
    }
}

/**
 * this function is a hook of a row in the table
 * @param props data for a row
 * @returns {JSX.Element} JSX element of the row
 */
function Row(props) {
    const { row } = props;
    const [open, setOpen] = React.useState(false);

    const delete_user_button_string = 'מחיקת משתמש';
    const change_password_button_string = 'שינוי סיסמה';
    const user_email_string = 'דוא"ל';
    const user_phone_number_string = 'מספר פלאפון';
    const city = 'עיר';
    const schools = 'בתי ספר';

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
                {/* the arrow to open the extra info */}
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>
                {/* main user's info */}
                <TableCell component="th" scope="row">
                    <Grid container spacing={1}>
                        <Grid item xs={2}><Avatar>{row.name.charAt(0)}</Avatar></Grid>
                        <Grid item xs={4}>{row.name}</Grid>
                    </Grid>
                </TableCell>
                <TableCell>{row.role}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1 }}>
                            {/* the user's general info */}
                            <List
                                sx={{
                                    width: '40%',
                                    bgcolor: 'background.paper',
                                }}
                            >
                                <ListItem>
                                    <ListItemText primary={user_email_string} secondary={row.email} />
                                </ListItem>
                                <Divider component="li" />
                                <ListItem>
                                    <ListItemText primary={user_phone_number_string} secondary={row.phoneNumber} />
                                </ListItem>
                                <Divider component="li" />
                                <ListItem>
                                    <ListItemText primary={city} secondary={row.city} />
                                </ListItem>
                                <Divider component="li" />
                                <ListItem>
                                    <ListItemText primary={schools} secondary={row.schools.reduce((previous, current) => previous + `${current}, `, '')} />
                                </ListItem>
                            </List>
                            <br/>
                            {/* the action buttons for each user */}
                            {/*TODO: have a warning beforehand and a verification */}
                            <Grid container spacing={1}>
                                <Grid item xs={2.5}><Button onClick={() => props.userDeletion(row.username)} color="error" variant="outlined">{delete_user_button_string}</Button></Grid>
                                <Grid item xs={2}><Button onClick={() => props.handleOpenCPDialog(row.username, row.name)} color="secondary" variant="outlined">{change_password_button_string}</Button></Grid>
                            </Grid>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
}

function ChangePasswordDialog(props){
    const [showPassword, setShowPassword] = useState(false);

    const title_string = "שינוי סיסמה עבור"
    const password_string = "סיסמה חדשה"
    const change_string = "שנה/י"

    /**
     * gathers the input and passing it to the provided callback
     * @param event the elements' status
     */
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        if (data.get("password") === ''){
            // todo: raise error
        }
        else{
            props.callback(props.selectedUser, data.get("password"))
        }

    }

    return (
        <Dialog fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle>{title_string} {props.selectedName}</DialogTitle>
            <Stack component="form" sx={{alignItems: "center"}} onSubmit={handleSubmit}>
                {/*the new password field*/}
                {/*todo: the adornment is on the wrong side for some reason*/}
                <TextField name="password"
                           sx={{paddingBottom: 1, width: "50%"}}
                           id="outlined-basic"
                           label={password_string}
                           variant="outlined"
                           type={showPassword ? 'text' : 'password'}
                           InputProps={{
                               endAdornment: (
                                   <InputAdornment position="end">
                                       <IconButton
                                           onClick={() => setShowPassword(!showPassword)}
                                           onMouseDown={(event) => event.preventDefault()}
                                       >
                                           {showPassword ? <VisibilityOff /> : <Visibility />}
                                       </IconButton>
                                   </InputAdornment>
                               ),
                           }}/>
                {/*the submit button*/}
                <Button type="submit" sx={{marginBottom: 1, width: "20%"}} variant="outlined">{change_string}</Button>
            </Stack>
        </Dialog>
    )
}

// data for offline testing
const rows = [
    createData("Ronit", 'רונית', "מפקחת", "ronit@post.bgu.ac.il", "000-123-4567", "פתח תקווה", ["מקיף ז'", "רגר"]),
    createData("Shoshi", 'שושי', "מדריכה", "shoshi@post.bgu.ac.il", "002-123-4567", "ירוחם", ["יהלום", "שהם"]),
];

export default function ManageUsers(){
    const [openCPDialog, setOpenCPDialog] = useState(false);
    const [selectedUser, setSelectedUser] = useState('');
    const [selectedName, setSelectedName] = useState('');

    const table_name_col_string = 'שם';
    const table_role_col_string = 'תפקיד';
    const page_title_string = 'ניהול משתמשים'

    let navigate = useNavigate();

    useEffect(() => {
        // TODO: ask server for the data
    }, []);

    /**
     * handler for the response from the server for table data
     * @param data the table data to arrange
     */
    const handleReceivedData = (data) => {
        // TODO: implement
    }

    /**
     * callback for the response of the server for the deletion of a user request
     * @param data
     */
    const userDeletionCallback = (data) => {
        console.log(data)
        // TODO: check this once the option to register users is working
    }

    /**
     * handler for deleting a user. sends a request to the server to delete the given user
     * @param username the user to delete
     */
    const handleUserDeletion = (username) => {
        Connection.getInstance().removeUser({
            currUser: window.sessionStorage.getItem('username'),
            userToRemove: username,
        }, userDeletionCallback)
    }

    /**
     * handles the opening of the dialog to change password to a selcted user
     * @param username the selected user
     * @param name the name of the selected user
     */
    const handleOpenCPDialog = (username, name) => {
        setOpenCPDialog(true);
        setSelectedUser(username);
        setSelectedName(name);
        //TODO: implement
    }

    /**
     * handles the closing of the dialog for changing the password for a selected user
     */
    const handleCloseCPDialog = () => {
        setOpenCPDialog(false);
    }

    /**
     * handler for changing the password of a user. sends a request to the server to change the password of the given user
     * @param username the user to change the password to
     * @param newPassword the new password of the user
     */
    const handleUserChangePassword = (username, newPassword) => {
        console.log('sup');
        setOpenCPDialog(false);
        // TODO: move to auth and send
    }

    return (
        <div id="Manage-users">
            <h1>{page_title_string}</h1>
            {/* adding new users button */}
            <div>
                <Button variant="outlined" color="secondary" onClick={() => navigate('../registerUsers')}>הוספת משתמש</Button>
            </div>
            <TableContainer id="Manage-users-table" component={Paper}>
                {/* the table */}
                <Table aria-label="collapsible table">
                    <TableHead>
                        <TableRow>
                            <TableCell />
                            <TableCell>{table_name_col_string}</TableCell>
                            <TableCell>{table_role_col_string}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row) => (
                            <Row key={row.username} row={row} handleOpenCPDialog={handleOpenCPDialog} userDeletion={handleUserDeletion} handleUserChangePassword={handleUserChangePassword}/>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            {/*TODO: add a dialog for the deletion*/}
            {/*change password dialog pop up*/}
            <ChangePasswordDialog
                selectedUser={selectedUser}
                selectedName={selectedName}
                open={openCPDialog}
                onClose={handleCloseCPDialog}
                callback={handleUserChangePassword}
            />
        </div>
    )
}