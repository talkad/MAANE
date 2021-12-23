import React, {useEffect, useState} from "react";
import './ManageUsers.css'

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import Box from "@mui/material/Box";
import {Avatar, Collapse, Divider, Grid, IconButton, List, ListItem, ListItemText} from "@mui/material";
import Button from "@mui/material/Button";
import UserInfo from "../../User/UserInfo";
import Connection from "../../Communication/Connection";


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
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>
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
                            {/*TODO: have a warning beforehand and a verification */}
                            <Grid container spacing={1}>
                                <Grid item xs={2.5}><Button onClick={() => props.userDeletion(row.username)} color="error" variant="outlined">{delete_user_button_string}</Button></Grid>
                                <Grid item xs={2}><Button onClick={() => props.handleUserChangePassword(row.username)} color="secondary" variant="outlined">{change_password_button_string}</Button></Grid>
                            </Grid>

                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
}


const rows = [
    createData("anakin", 'אנקין', "בוגד", "stam@post.bgu.ac.il", "000-123-4567", "פתח תקווה", ["מקיף ז'", "רגר"]),
    createData("kenobi", 'קנובי', "גנרל", "stam2@post.bgu.ac.il", "002-123-4567", "ירוחם", ["מאיפה לי", "איזה מקיף בלוד"]),
];

export default function ManageUsers(){

    const table_name_col_string = 'שם';
    const table_role_col_string = 'תפקיד';

    useEffect(() => {
        console.log("typeeeeeeee");
        console.log(window.sessionStorage.getItem('type'));
    }, []);

    const handleUserDeletion = (username) => {
        console.log('hi there');
        // TODO: send
    }

    const handleUserChangePassword = (username) => {
        console.log('sup');
        // TODO: send
    }

    return (
        <div id="Manage-users">
            <h1>manage'em</h1>
            <div><Button variant="outlined" color="secondary" onClick={() => document.location.href = window.location.origin + '/user/registerUsers'}>הוספת משתמש</Button></div>
            <TableContainer id="Manage-users-table" component={Paper}>
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
                            <Row key={row.username} row={row} userDeletion={handleUserDeletion} handleUserChangePassword={handleUserChangePassword}/>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    )
}