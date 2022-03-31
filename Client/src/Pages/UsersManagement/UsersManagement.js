import React, {useEffect, useState} from "react";
import './UsersManagement.css'

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
    Alert, Autocomplete,
    Avatar,
    Collapse,
    Dialog,
    DialogTitle,
    Divider,
    Grid,
    IconButton, InputAdornment,
    List,
    ListItem,
    ListItemText, Stack, TextField, Tooltip, Typography
} from "@mui/material";
import Button from "@mui/material/Button";
import {useNavigate} from "react-router-dom";
import * as Space from 'react-spaces';
import Connection from "../../Communication/Connection";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";
import DeleteIcon from '@mui/icons-material/Delete';


/**
 * a function to return an object of the data the tables accepts
 * @param username username of the user
 * @param name the full name of the user
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

    // actions
    const delete_user_button_string = 'מחיקת משתמש';
    const change_password_button_string = 'שינוי סיסמה';
    const edit_schools_button_string = 'עריכת בתי ספר';

    // data
    const user_email_string = 'דוא"ל';
    const user_phone_number_string = 'מספר פלאפון';
    const city = 'עיר';
    const schools = 'בתי ספר';
    const actions = 'פעולות:';

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
            {/*secondary user's into*/}
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
                                <Divider component="li" />
                                <ListItem>
                                    <ListItemText primary={actions}/>
                                </ListItem>
                            </List>
                            <br/>
                            {/* the action buttons for each user */}
                            <Grid sx={{marginBottom: 1}} container spacing={1}>
                                {/*editing actions*/}
                                <Grid item xs={1.5}><Button fullWidth onClick={() => props.handleOpenCPDialog(row.username, row.name)} color="secondary" variant="outlined">{change_password_button_string}</Button></Grid>
                                <Grid item xs={1.5}><Button fullWidth onClick={() => props.handleOpenEditSchoolsDialog(row.username, row.name, row.schools)} color="secondary" variant="outlined">{edit_schools_button_string}</Button></Grid>
                            </Grid>
                            <Grid container spacing={1}>
                                {/*removing user*/}
                                <Grid item xs={1.5}><Button fullWidth onClick={() => props.handleOpenDeleteDialog(row.username, row.name)} color="error" variant="outlined">{delete_user_button_string}</Button></Grid>
                            </Grid>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
}

/**
 * a dialog element for removing a user
 * @param props the properties the element gets
 * @returns {JSX.Element} the element
 */
function DeleteUserDialog(props){

    const title_string = "האם את/ה בטוח/ה שהינך רוצה למחוק את";
    const delete_string = "מחק/י";
    const cancel_string = "ביטול";

    const handleSubmitDeletion = () => {
        props.callback(props.selectUser);
    }

    return (
        <Dialog titleStyle={{textAlign: "center"}} sx={{alignItems: "right"}} fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle><Typography variant="h5" align="center">?{title_string} {props.selectedName}</Typography></DialogTitle>
            <Grid container justifyContent="center" spacing={0}>
                <Grid item align="center" xs={6}>
                    {/*the cancel button*/}
                    <Button onClick={() => props.onClose()} sx={{marginBottom: 1, width: "50%"}} variant="outlined">{cancel_string}</Button>
                </Grid>
                <Grid item align="center" xs={6}>
                    {/*the delete button*/}
                    <Button onClick={() => handleSubmitDeletion()} sx={{marginBottom: 1, width: "50%"}} color="error" variant="outlined">{delete_string}</Button>
                </Grid>
            </Grid>
        </Dialog>
    )
}

/**
 * a dialog element for changing a password for a user
 * @param props the properties the element gets
 * @returns {JSX.Element} the element
 */
function ChangePasswordDialog(props){
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const title_string = "שינוי סיסמה עבור";
    const password_string = "סיסמה חדשה";
    const confirm_password_string = "אימות סיסמה";
    const change_string = "שנה/י";
    const cancel_string = "ביטול";

    /**
     * gathers the input and passing it to the provided callback
     * @param event the elements' status
     */
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        if (data.get("password") === '' || data.get('confirmPassword') === ''){
            // todo: raise error
        }
        else{
            if (data.get('password') === data.get('confirmPassword')){
                props.callback(props.selectedUser, data.get("password"), data.get("confirmPassword"));
            }
            else{
                // todo: raise error
            }
        }
    }

    return (
        <Dialog fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle><Typography variant="h5" align="center">{title_string} {props.selectedName}</Typography></DialogTitle>
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
                {/*confirm password*/}
                <TextField name="confirmPassword"
                           sx={{paddingBottom: 1, width: "50%"}}
                           label={confirm_password_string}
                           variant="outlined"
                           type={showConfirmPassword ? 'text' : 'password'}
                           InputProps={{
                               endAdornment: (
                                   <InputAdornment position="end">
                                       <IconButton
                                           onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                           onMouseDown={(event) => event.preventDefault()}
                                       >
                                           {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                                       </IconButton>
                                   </InputAdornment>
                               ),
                           }}/>
                {/*the submit button*/}
                <Grid container justifyContent="center" spacing={0}>
                    <Grid item align="center" xs={4}>
                        {/*the cancel button*/}
                        <Button onClick={() => props.onClose()} sx={{marginBottom: 1, width: "50%"}} variant="outlined">{cancel_string}</Button>
                    </Grid>
                    <Grid item align="center" xs={4}>
                        {/*the change button*/}
                        <Button type="submit" color="success" sx={{marginBottom: 1, width: "50%"}} variant="outlined">{change_string}</Button>
                    </Grid>
                </Grid>
            </Stack>
        </Dialog>
    )
}

// offline mock data for general testing of the Autocomplete component
const schools = [
    { label: "סתם", id: 1},
    { label: "כלום", id: 2},
]

/**
 * a dialog element for editing the assigned school of a user
 * @param props the properties the element gets
 * @returns {JSX.Element} the element
 */
function EditSchoolsDialog(props){
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const [schoolSearchValue, setSchoolSearchValue] = useState('');
    const [selectedSchoolId, setSelectedSchoolId] = useState(-1);

    const title_string = "עריכת בתי ספר תחת";
    const search_school_string = "חפש/י בית ספר";
    const add_school_button_string = "הוספת בית ספר";

    // tooltips' strings
    const delete_tooltip_string = "הסרת בית ספר";


    const addSchool = () => {
        if (selectedSchoolId === -1){
            setError(true);
            setErrorMessage("נא לבחור בית ספר מהרשימה")
        }
        else{
            setError(false);
            setSchoolSearchValue('');
            setSelectedSchoolId(-1);
            props.addSchoolCallback(props.selectedUser, schoolSearchValue, selectedSchoolId);
        }
    }

    return (
        <Dialog fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle><Typography variant="h5" align="center">{title_string} {props.selectedName}</Typography></DialogTitle>
            <Stack sx={{alignItems: "center"}}>
                {/*a list of the current schools assigned to the user*/}
                <List sx={{width: "50%"}}>
                    {props.selectedSchools.map((school) =>
                        <div>
                            {/*representation of each school*/}
                            <ListItem
                                secondaryAction={
                                    <Tooltip title={delete_tooltip_string}>
                                        <IconButton onClick={() => props.removeSchoolCallback(props.selectedUser, school.id)} edge="end">
                                            <DeleteIcon />
                                        </IconButton>
                                    </Tooltip>
                                }
                            >
                                <ListItemText
                                    primary={school}
                                />

                            </ListItem>
                            <Divider component="li" />
                        </div>
                    )}
                    <ListItem>
                        {/*field for adding new school to a givel user*/}
                        {/*<TextField*/}
                        {/*    value={addSchoolName}*/}
                        {/*    onChange={handleAddSchoolNameChange}*/}
                        {/*    error={error}*/}
                        {/*    fullWidth*/}
                        {/*    InputProps={{*/}
                        {/*        endAdornment: (*/}
                        {/*            <InputAdornment position="end">*/}
                        {/*                <Tooltip title={add_tooltip_string}>*/}
                        {/*                    <IconButton*/}
                        {/*                        onClick={addSchool}*/}
                        {/*                        onMouseDown={(event) => event.preventDefault()}*/}
                        {/*                    >*/}
                        {/*                        <AddIcon color="primary"/>*/}
                        {/*                    </IconButton>*/}
                        {/*                </Tooltip>*/}
                        {/*            </InputAdornment>*/}
                        {/*        ),*/}
                        {/*    }}/>*/}

                        <Autocomplete
                            disablePortal
                            value={schoolSearchValue}
                            onChange={function (event, newValue){
                                    if (newValue !== null){
                                        setSchoolSearchValue(newValue.label);
                                        setSelectedSchoolId(newValue.id);
                                    }
                                }
                            }
                            options={schools}
                            // todo: is the below thingy bad?
                            isOptionEqualToValue={(option, value) => true}
                            fullWidth
                            renderInput={(params) => <TextField {...params} label={search_school_string} error={error} />}
                        />
                    </ListItem>
                    <ListItem style={{display:'flex', justifyContent:'flex-end'}}>
                        <Button onClick={addSchool} disabled={selectedSchoolId === -1} variant='contained'>{add_school_button_string}</Button>
                    </ListItem>
                </List>
                {error && <Alert sx={{marginBottom: 1}} severity="error">{errorMessage}</Alert>}
            </Stack>
        </Dialog>
    )
}

// data for offline testing
const rows = [
    createData("Ronit", 'רונית שושי', "מפקחת", "ronit@post.bgu.ac.il", "000-123-4567", "פתח תקווה", ["מקיף ז'", "רגר"]),
    createData("Shoshi", 'שושי רונית', "מדריכה", "shoshi@post.bgu.ac.il", "002-123-4567", "ירוחם", ["יהלום", "שהם"]),
];

export default function UsersManagement(props){
    const [tableRows, setTableRows] = useState(rows);

    // dialogs states
    const [openCPDialog, setOpenCPDialog] = useState(false);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [openEditSchoolsDialog, setOpenEditSchoolsDialog] = useState(false);
    const [selectedUser, setSelectedUser] = useState('');
    const [selectedName, setSelectedName] = useState('');
    const [selectedSchools, setSelectedSchools] = useState([]);

    // snackbar states
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [snackbarSeverity, setSnackbarSeverity] = useState('');
    const [snackbarMessage, setSnackbarMessage] = useState('');

    const table_name_col_string = 'שם';
    const table_role_col_string = 'תפקיד';
    const page_title_string = 'ניהול משתמשים';

    let navigate = useNavigate();

    useEffect(() => {
        props.setHideBars(false);
        if(props.userType === "SUPERVISOR"){
            Connection.getInstance().getAppointedUsers(window.sessionStorage.getItem('username'), handleReceivedData);
        }
        else if(props.userType === "SYSTEM_MANAGER"){
            Connection.getInstance().getAllUsers(window.sessionStorage.getItem('username'), handleReceivedData); // TODO: check this (logging in with admin)
        }
        // todo: see about how i get the information about the school and how i arrange it

    }, []);


    /**
     * handler for the response from the server for the table data
     * @param data the table data to arrange
     */
    const handleReceivedData = (data) => {
        if (!data.failure){
            let rows = [];

            for (const row of data.result){
                let role = "";

                if (row.userStateEnum === "INSTRUCTOR") {
                    role = "מדריכ/ה";
                }
                else if (row.userStateEnum === "SUPERVISOR") {
                    role = "מפקח/ת";
                }
                else if (row.userStateEnum === "GENERAL_SUPERVISOR") {
                    role = "מפקח/ת כללי/ת";
                }

                // for live updating of the dialog while adding school to a user
                if (selectedUser === row.currUser){
                    setSelectedSchools(row.schools);
                }

                // not including admins in the table
                if(row.currUser === 'admin'){ //todo: is that considered hardcoded?
                    continue;
                }

                rows.push(createData(
                    row.currUser,
                    row.firstName + " " + row.lastName,
                    role,
                    row.email,
                    row.phoneNumber,
                    row.city,
                    row.schools
                ));
            }

            setTableRows(rows);
        }
        else {
            //TODO: needed?
        }
    }

    /**
     * refreshes the new table
     */
    const refreshData = () => {
        if(props.userType === "SUPERVISOR"){
            Connection.getInstance().getAppointedUsers(window.sessionStorage.getItem('username'), handleReceivedData);
        }
        else if(props.userType === "SYSTEM_MANAGER"){
            Connection.getInstance().getAllUsers(window.sessionStorage.getItem('username'), handleReceivedData); // TODO: check this (logging in with admin)
        }

        //TODO: have a loading animation
    }

    // USER DELETION DIALOG

    /**
     * handles the opening of the dialog to delete a selected user
     * @param username the selected user
     * @param name the name of the selected user
     */
    const handleOpenDeleteDialog = (username, name) => {
        setOpenDeleteDialog(true);
        setSelectedUser(username);
        setSelectedName(name);
    }

    /**
     * handles the closing of the dialog for deleting a selected user
     */
    const handleCloseDeleteDialog = () => {
        setOpenDeleteDialog(false);
    }

    /**
     * callback for the response of the server for the deletion of a user request
     * @param data the response from the server
     */
    const userDeletionCallback = (data) => {
        props.setAuthAvailability(false);
        console.log(data)
        // TODO: do something
    }

    /**
     * handler for deleting a user. sends a request to the server to delete the given user
     * @param username the user to delete
     */
    const handleUserDeletion = (username) => {
        console.log("please don't delete me");

        props.setAuthCallBack(() => () =>
            Connection.getInstance().removeUser(window.sessionStorage.getItem('username'), username, userDeletionCallback)
        );
        props.setAuthAvailability(true);
        props.setAuthCalleePage('../home');
        props.setAuthGoToPage('../home');
        navigate(`../auth`, {replace: true})
    }

    // CHANGE USER PASSWORD DIALOG

    /**
     * handles the opening of the dialog to change password to a selected user
     * @param username the selected user
     * @param name the name of the selected user
     */
    const handleOpenCPDialog = (username, name) => {
        setOpenCPDialog(true);
        setSelectedUser(username);
        setSelectedName(name);
    }

    /**
     * handles the closing of the dialog for changing the password for a selected user
     */
    const handleCloseCPDialog = () => {
        setOpenCPDialog(false);
    }

    /**
     * callback for the response of the server for the changing a password for a user
     * @param data the response from the server
     */
    const userChangePasswordCallback = (data) => {
        props.setAuthAvailability(false);
        //TODO: doesn't work cause the page is not loaded when this part runs
        if (!data.failure){
            setSnackbarSeverity('success');
            setSnackbarMessage('הסיסמה שונתה בהצלחה');
            setOpenSnackbar(true);
        }
        else{
            setSnackbarSeverity('error');
            setSnackbarMessage('הסיסמה לא שונתה'); // todo: better error?
            setOpenSnackbar(true);
        }
    }

    /**
     * handler for changing the password of a user. sends a request to the server to change the password of the given user
     * @param username the user to change the password to
     * @param newPassword the new password of the user
     * @param newPasswordConfirmation confirmation of the password
     */
    const handleUserChangePassword = (username, newPassword, newPasswordConfirmation) => {
        setOpenCPDialog(false);

        props.setAuthCallBack(() => () => Connection.getInstance().changePasswordToUser(window.sessionStorage.getItem('username'),
            username, newPassword, newPasswordConfirmation, userChangePasswordCallback));
        props.setAuthAvailability(true);
        props.setAuthCalleePage('../home');
        props.setAuthGoToPage('../home');
        navigate(`../auth`, {replace: true})
    }

    // EDIT SCHOOLS DIALOG

    /**
     * handles the opening of the dialog to edit the assigned school of a selected user
     * @param username the selected user
     * @param name the name of the selected user
     * @param schools the schools of the selected user
     */
    const handleOpenEditSchoolsDialog = (username, name, schools) => {
        setOpenEditSchoolsDialog(true);
        setSelectedUser(username);
        setSelectedName(name);
        setSelectedSchools(schools)
    }

    /**
     * handles the closing of the dialog for editing the assigned schools of a selected user
     */
    const handleCloseEditSchoolsDialog = () => {
        setOpenEditSchoolsDialog(false);
    }

    /**
     * a callback for the response from the server regarding adding an assigned school to a user request
     * @param data the response from the server
     */
    const userAddSchoolCallback = (data) => {
        if (!data.failure){
            setSnackbarSeverity('success');
            setSnackbarMessage('בית הספר הוסף בהצלחה למשתמש');
            setOpenSnackbar(true);
        }
        else {
            setSnackbarSeverity('error');
            setSnackbarMessage('הפעולה להוספת בית ספר למשתמש נכשלה'); // todo: better error?
            setOpenSnackbar(true);
            refreshData();
        }
    }

    /**
     * handler for sending a request to add a school assigned to a selected user
     * @param username the selected user
     * @param schoolName the name of the school to assign to the selected user
     * @param schoolId the id of the school to assign to the selected user
     */
    const handleUserAddSchool = (username, schoolName, schoolId) => {
        // todo: send
    }

    /**
     * a callback for the response from the server regarding removing an assigned school from a user request
     * @param data the response from the server
     */
    const userRemoveSchoolCallback = (data) => {
        if (!data.failure){
            setSnackbarSeverity('success');
            setSnackbarMessage('בית הספר הוסר בהצלחה מהמשתמש');
            setOpenSnackbar(true);
            refreshData();
        }
        else {
            setSnackbarSeverity('error');
            setSnackbarMessage('הפעולה להסרת בית ספר ממשתמש נכשלה'); // todo: better error?
            setOpenSnackbar(true);
        }
    }

    /**
     * handler for sending a request to add a school assigned to a selected user
     * @param username the selected user
     * @param schoolId the id of the school to remove from the selected user
     */
    const handleUserRemoveSchool = (username, schoolId) => {
        // todo: send
    }


    return (
        <Space.Fill scrollable>
            <div id="Manage-users">
                <h1>{page_title_string}</h1>
                {/* adding new users button */}
                <div>
                    <Button variant="outlined" color="secondary" onClick={() => navigate('../registerUsers')}>הוספת משתמש</Button>
                </div>
                <TableContainer id="Manage-users-table" component={Paper}>
                    {/* the table */}
                    {/*TODO: implement a case for an empty table*/}
                    <Table aria-label="collapsible table">
                        <TableHead>
                            <TableRow>
                                <TableCell />
                                <TableCell>{table_name_col_string}</TableCell>
                                <TableCell>{table_role_col_string}</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {tableRows.map((tableRows) => (
                                <Row key={tableRows.username} row={tableRows} handleOpenEditSchoolsDialog={handleOpenEditSchoolsDialog} handleOpenCPDialog={handleOpenCPDialog} handleOpenDeleteDialog={handleOpenDeleteDialog}/>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>

                {/*change password dialog pop up*/}
                <ChangePasswordDialog
                    selectedUser={selectedUser}
                    selectedName={selectedName}
                    open={openCPDialog}
                    onClose={handleCloseCPDialog}
                    callback={handleUserChangePassword}
                />

                {/*delete user dialog pop up*/}
                <DeleteUserDialog
                    selectUser={selectedUser}
                    selectedName={selectedName}
                    open={openDeleteDialog}
                    onClose={handleCloseDeleteDialog}
                    callback={handleUserDeletion}
                />

                {/*edit schools dialog pop up*/}
                <EditSchoolsDialog
                    selectedUser={selectedUser}
                    selectedName={selectedName}
                    selectedSchools={selectedSchools}
                    open={openEditSchoolsDialog}
                    onClose={handleCloseEditSchoolsDialog}
                    addSchoolCallback={handleUserAddSchool}
                    removeSchoolCallback={handleUserRemoveSchool}/>

                {/*snackbar for notification on actions*/}
                <NotificationSnackbar
                    open={openSnackbar}
                    setOpen={setOpenSnackbar}
                    severity={snackbarSeverity}
                    message={snackbarMessage}/>
            </div>
        </Space.Fill>
    )
}