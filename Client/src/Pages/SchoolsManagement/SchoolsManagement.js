import * as Space from 'react-spaces';
import {
    Box,
    Collapse, Divider, Grid,
    IconButton,
    List, ListItem, ListItemText,
    Paper,
    Table, TableBody, TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import React, {useEffect, useState} from "react";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";

//css
import "./SchoolsManagement.css"

/**
 * a function that returns an object containing the data for a row in the table
 * @param id the id of the school
 * @param name the name of the school
 * @param address the address of the school
 * @param supervisor the supervisor or instructor of the school //TODO ask shaked if it's OR or AND
 * @param educationLevel the education level of the school
 * @param sector the sector of the school
 * @param numOfPupils the number of pupils in the school
 * @param phoneNumber the phone number of the school
 * @param educationType the education type of the school
 * @param city the city in which the school is located
 * @param cityForMail the idk //TODO ask shaked
 * @param principleName the name of the principle of the school
 * @param zipCode the zip code of the shcool
 * @param supervisionType the type of supervision of the school is under
 * @param secretaryMail the email address of the secretary office of the school
 * @returns {{zipCode, address, educationType, city, supervisionType, phoneNumber, educationLevel, principleName, name, numOfPupils, id, sector, secretaryMail, supervisor, cityForMail}}
 */
function createData(id, name, address, supervisor, educationLevel, sector, numOfPupils, phoneNumber, educationType,
                    city, cityForMail, principleName, zipCode, supervisionType, secretaryMail) {
    return {
        id,
        name,
        address,
        supervisor,
        educationLevel,
        sector,
        numOfPupils,
        phoneNumber,
        educationType,
        city,
        cityForMail,
        principleName,
        zipCode,
        supervisionType,
        secretaryMail,
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

    // administrative info strings
    const administrative_info_string = "מידע אדמיניסטרטיבי:";
    const principle_name_string = "שם מנהל";
    const address_primary_string = "כתובת";
    const phone_number_primary_string = "מספר טלפון";
    const zip_primary_string = "מיקוד";
    const secretary_office_email_primary_string = 'דוא"ל מזכירות';
    const city_for_mails_primary_string = "יישוב למכתבים";

    // educational info strings
    const educational_info_string = "מידע חינוכי:";
    const number_of_pupils_primary_string = "מספר תלמידים";
    const education_level_primary_string = "שלב חינוך";
    const education_type_primary_string = "סוג חינוך";
    const sector_primary_string = "מגזר";
    const supervisor_primary_string = "מפקח/מדריך";
    const supervision_type_primary_string = "סוג פיקוח";

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
                {/* main school's info */}
                <TableCell>{row.id}</TableCell>
                <TableCell>{row.name}</TableCell>
                <TableCell>{row.city}</TableCell>
            </TableRow>
            {/*secondary school's into*/}
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0}} colSpan={6}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Grid container spacing={1}>
                            <Grid item xs={6}>
                                <Box sx={{ margin: 1}}>
                                    <List
                                        sx={{
                                            width: "40%",
                                            bgcolor: "background.paper",
                                        }}
                                    >
                                        <ListItem>
                                            <ListItemText primary={administrative_info_string} />
                                        </ListItem>
                                        <ListItem>
                                            <ListItemText primary={principle_name_string} secondary={row.principleName} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={address_primary_string} secondary={row.address} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={zip_primary_string} secondary={row.zipCode} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={phone_number_primary_string} secondary={row.phoneNumber} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={secretary_office_email_primary_string} secondary={row.secretaryMail} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={city_for_mails_primary_string} secondary={row.cityForMail} />
                                        </ListItem>
                                    </List>
                                </Box>
                            </Grid>
                            <Grid item xs={6}>
                                <Box sx={{ margin: 1}}>
                                    <List
                                        sx={{
                                            width: "40%",
                                            bgcolor: "background.paper",
                                        }}
                                    >
                                        <ListItem>
                                            <ListItemText primary={educational_info_string} />
                                        </ListItem>
                                        <ListItem>
                                            <ListItemText primary={number_of_pupils_primary_string} secondary={row.numOfPupils} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={education_level_primary_string} secondary={row.educationLevel} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={education_type_primary_string} secondary={row.educationType} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={sector_primary_string} secondary={row.sector} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={supervisor_primary_string} secondary={row.supervisor} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={supervision_type_primary_string} secondary={row.supervisionType} />
                                        </ListItem>
                                    </List>
                                </Box>
                            {/*    TODO: add coordinator button*/}
                            </Grid>
                        </Grid>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    )
}

const rows = [
    createData(432, "hello there", "grove street", "ronit", "elementary",
        "idk", 420, "050-1234567", "lame", "narnia", "idk",
        "shula","35423", "super supervision", "hahawhoreadsthis@lmao.lol")
]

export default function SchoolsManagement(props){
    const [schools, setSchools] = useState(rows);

    // STRINGS
    const page_title = "בתי הספר שלי";
    const school_id_cell_head_string = "סמל מוסד";
    const school_name_cell_head_string = "שם מוסד";
    const school_city_cell_head_string = "עיר מוסד";

    useEffect(() => {
        // todo: get the schools from the server

        },[]);

    const arrangeSchoolCallback = (data) => {
        if (!data.failure){
            setSchools(data.result);
        }
        else{
            // todo: needed?
        }
    }

    return (
        <Space.Fill scrollable>
            <div id="Schools-management">
                {/*title*/}
                <h1>{page_title}</h1>
                {/*the table presenting the schools*/}
                <TableContainer sx={{width: "70%", marginTop: "1%"}} component={Paper}>
                    <Table aria-label="collapsible table">
                        {/*the table head containing the various headers*/}
                        <TableHead>
                            <TableRow>
                                <TableCell/>
                                <TableCell>{school_id_cell_head_string}</TableCell>
                                <TableCell>{school_name_cell_head_string}</TableCell>
                                <TableCell>{school_city_cell_head_string}</TableCell>
                            </TableRow>
                        </TableHead>
                        {/*the body of the table containing the rows*/}
                        <TableBody>
                            {schools.map((tableRow) => (
                                <Row key={tableRow.id} row={tableRow}/>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </div>
        </Space.Fill>

    )
}