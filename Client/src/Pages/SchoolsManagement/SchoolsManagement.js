import * as Space from 'react-spaces';
import {
    Alert, Autocomplete,
    Box, Button,
    Collapse, Dialog, DialogTitle, Divider, Grid,
    IconButton, InputAdornment,
    List, ListItem, ListItemText,
    Paper, Skeleton, Stack,
    Table, TableBody, TableCell,
    TableContainer,
    TableHead,
    TableRow, TextField, Typography
} from "@mui/material";
import React, {useEffect, useState} from "react";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";

//css
import "./SchoolsManagement.css"
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";
import Connection from "../../Communication/Connection";
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";

/**
 * a function that returns an object containing the data for a row in the table
 * @param id the id of the school
 * @param name the name of the school
 * @param address the address of the school
 * @param instructor the supervisor or instructor of the school
 * @param educationLevel the education level of the school
 * @param sector the sector of the school
 * @param numOfPupils the number of pupils in the school
 * @param phoneNumber the phone number of the school
 * @param educationType the education type of the school
 * @param city the city in which the school is located
 * @param cityForMail the idk
 * @param principleName the name of the principle of the school
 * @param zipCode the zip code of the shcool
 * @param supervisionType the type of supervision of the school is under
 * @param secretaryMail the email address of the secretary office of the school
 * @param coordinators list of the info of coordinators of the school
 * @returns {{zipCode, address, educationType, city, supervisionType, phoneNumber, educationLevel, principleName, name, numOfPupils, id, sector, secretaryMail, supervisor, cityForMail}}
 */
function createData(id, name, address, instructor, educationLevel, sector, numOfPupils, phoneNumber, educationType,
                    city, cityForMail, principleName, zipCode, supervisionType, secretaryMail, coordinators) {
    return {
        id,
        name,
        address,
        instructor,
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
        coordinators,
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
    const supervisor_primary_string = "מפקח";
    const supervision_type_primary_string = "סוג פיקוח";

    // coordinator's strings
    const coordinator_title_string = "רכזים:";
    const coordinator_name_primary_string = "שם";
    const coordinator_email_primary_string = 'דוא"ל';
    const coordinator_phone_number_primary_string = "מספר טלפון";
    const remove_coordinator_button_string = "הסרת רכז/ת";

    // action strings
    const action_title_string = "פעולות:";
    const add_coordinator_string = "הוספת רכז/ת";

    return (
        <React.Fragment>
            <Paper sx={{width: "70%"}} elevation={3}>
                        <Grid container spacing={1}>
                            <Grid sx={{margin: 1}} item xs={12}>
                                <Typography variant={'h3'}>{row.name} ({row.id}), {row.city}</Typography>
                            </Grid>
                            <Grid item xs={6}>
                                <Box sx={{ margin: 1}}>
                                    <List
                                        sx={{
                                            width: "40%",
                                            bgcolor: "background.paper",
                                        }}
                                    >
                                        {/*administrative info*/}
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
                                        {/*educational info*/}
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
                                            <ListItemText primary={supervisor_primary_string} secondary={row.instructor} />
                                        </ListItem>
                                        <Divider component="li" />
                                        <ListItem>
                                            <ListItemText primary={supervision_type_primary_string} secondary={row.supervisionType} />
                                        </ListItem>
                                    </List>
                                </Box>
                            </Grid>
                            <Grid sx={{margin: 1}} item xs={12}>
                                <Typography>{coordinator_title_string}</Typography>
                            </Grid>

                            {/*coordinator info*/}
                            <Grid sx={{margin: 1}} item xs={12}>
                                <List>
                                    {row.coordinators.map((coordinator) =>
                                        <ListItem>
                                            <ListItemText primary={coordinator_name_primary_string} secondary={coordinator.firstName + " " + coordinator.lastName} />
                                            <ListItemText primary={coordinator_email_primary_string} secondary={coordinator.email} />
                                            <ListItemText primary={coordinator_phone_number_primary_string} secondary={coordinator.phoneNumber} />
                                            <Button id={`remove_coordinator_${coordinator.email}`} onClick={() => props.handleOpenRemoveCoordinatorDialog(coordinator.firstName + " " + coordinator.lastName, row.id)} variant="outlined" color="error">{remove_coordinator_button_string}</Button>
                                        </ListItem>)}
                                </List>
                            </Grid>

                            {/*actions*/}
                            <Grid sx={{margin: 1}} item xs={12}>
                                <Typography>{action_title_string}</Typography>
                            </Grid>
                            <Grid sx={{margin: 1}} item xs={6}>
                                <Button id={`school_add_coordinator_button_${row.id}`} onClick={() => props.handleOpenAddCoordinatorDialog(row.id, row.name)} variant="outlined" sx={{marginBottom: 1}}>{add_coordinator_string}</Button>
                            </Grid>
                        </Grid>
            </Paper>
        </React.Fragment>
    )
}

/**
 * a dialog element for adding a coordinator to a selected school
 * @param props the properties the element gets
 * @returns {JSX.Element} the element
 */
function AddCoordinatorDialog(props){
    const [values, setValues] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
    });

    const [error, setError] = useState(false);

    // strings
    const title_string = "הוספת מפקח/ת עבור בית ספר";
    const first_name_label_string = "שם פרטי";
    const last_name_label_string = "שם משפחה";
    const email_label_string = 'דוא"ל';
    const phone_number_label_string = "מספר פלאפון";

    const error_message_string = "נא למלא את כל השדות";
    const submit_button_string = "הוספ/י";
    const cancel_button_string = "ביטול";

    /**
     * onChange handler for the text-fields
     * @param props the field who's been changed
     * @returns {(function(*): void)|*} updating the new values in the state hook
     */
    const handleTextFieldsChange = (props) => (event) => {
        setValues({ ...values, [props]: event.target.value})
    }

    /**
     * gathers the input and passing it to the provided callback
     * @param event the elements' status
     */
    const handleSubmit = (event) => {
        event.preventDefault();

        if (values.firstName === '' || values.lastName === '' || values.email === '' || values.phoneNumber === ''){
            setError(true);
        }
        else{
            //TODO: how do i get the work field
            setError(false);
            props.callback("work field. how do i get this??", values.firstName, values.lastName, values.email, values.phoneNumber, props.schoolID);
        }
    }

    return (
        <Dialog fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle><Typography variant="h5" align="center">{title_string} {props.schoolName} ({props.schoolID})</Typography></DialogTitle>

            <Stack component="form" sx={{alignItems: "center"}} onSubmit={handleSubmit}>
                <Grid container spacing={1}>
                    <Collapse in={error}>
                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            <Alert id={"add_coordinator_alert"} severity="error">{error_message_string}</Alert>
                        </Grid>
                    </Collapse>

                    <Grid item xs={12}  sx={{marginRight: "3%", marginLeft: "3%"}}>
                        <Grid container spacing={1}>
                            {/*first name*/}
                            <Grid item xs={6}>
                                <TextField
                                    id={"add_coordinator_first_name"}
                                    value={values.firstName}
                                    onChange={handleTextFieldsChange("firstName")}
                                    label={first_name_label_string}
                                    error={error}
                                    required
                                    fullWidth
                                />
                            </Grid>
                            {/*last name*/}
                            <Grid item xs={6}>
                                <TextField
                                    id={"add_coordinator_last_name"}
                                    value={values.lastName}
                                    onChange={handleTextFieldsChange("lastName")}
                                    label={last_name_label_string}
                                    error={error}
                                    required
                                    fullWidth
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                    {/*email*/}
                    <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                        <TextField
                            id={"add_coordinator_email"}
                            value={values.email}
                            onChange={handleTextFieldsChange("email")}
                            label={email_label_string}
                            error={error}
                            required
                            fullWidth
                        />
                    </Grid>
                    {/*phone number*/}
                    <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                        <TextField
                            id={"add_coordinator_phone_number"}
                            value={values.phoneNumber}
                            onChange={handleTextFieldsChange("phoneNumber")}
                            label={phone_number_label_string}
                            error={error}
                            required
                            fullWidth
                        />
                    </Grid>
                </Grid>
                <Button id={"add_coordinator_submit_button"} type="submit" color="primary" variant="contained" sx={{width: "40%", marginBottom: "1%", marginTop: "1%"}}>{submit_button_string}</Button>

                <Button id={"add_coordinator_cancel_button"} color="error" onClick={() => props.onClose()} variant="contained" sx={{width: "40%", marginBottom: "1%", marginTop: "1%"}}>{cancel_button_string}</Button>
            </Stack>
        </Dialog>
    )
}

/**
 * a dialog element for removing a coordinator
 * @param props the properties the element gets
 * @returns {JSX.Element} the element
 */
function RemoveCoordinatorDialog(props){

    const title_string = "האם את/ה בטוח/ה שהינך רוצה למחוק את הרכז/ת";
    const delete_string = "מחק/י";
    const cancel_string = "ביטול";

    const handleSubmitDeletion = () => {
        props.callback("dunno the work field", props.schoolID); // TODO: know the work field
    }

    return (
        <Dialog titleStyle={{textAlign: "center"}} sx={{alignItems: "right"}} fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle><Typography variant="h5" align="center">?{title_string} {props.name}</Typography></DialogTitle>
            <Grid container justifyContent="center" spacing={0}>
                <Grid item align="center" xs={6}>
                    {/*the cancel button*/}
                    <Button id={"remove_coordinator_submit_button"} onClick={() => props.onClose()} sx={{marginBottom: 1, width: "50%"}} variant="outlined">{cancel_string}</Button>
                </Grid>
                <Grid item align="center" xs={6}>
                    {/*the delete button*/}
                    <Button id={"remove_coordinator_cancel_button"} onClick={() => handleSubmitDeletion()} sx={{marginBottom: 1, width: "50%"}} color="error" variant="outlined">{delete_string}</Button>
                </Grid>
            </Grid>
        </Dialog>
    )
}

const rows = [
    createData(432, "hello there", "grove street", "ronit", "elementary",
        "idk", 420, "050-1234567", "lame", "narnia", "idk",
        "shula","35423", "super supervision", "hahawhoreadsthis@lmao.lol",
        [{firstName: "a", lastName: "b", email: "idk@post.lol", phoneNumber: "050-lmao"},
            {firstName: "c", lastName: "d", email: "idk2@post.lol", phoneNumber: "054-lmao"}]),
    createData(431, "hello there", "grove street", "ronit", "elementary",
        "idk", 420, "050-1234567", "lame", "narnia", "idk",
        "shula","35423", "super supervision", "hahawhoreadsthis@lmao.lol",
        [{firstName: "c", lastName: "d", email: "idk2@post.lol", phoneNumber: "054-lmao"}])
]

const mockSchool = createData(432, "hello there", "grove street", "ronit", "elementary",
    "idk", 420, "050-1234567", "lame", "narnia", "idk",
    "shula","35423", "super supervision", "hahawhoreadsthis@lmao.lol",
    [{firstName: "a", lastName: "b", email: "idk@post.lol", phoneNumber: "050-lmao"},
        {firstName: "c", lastName: "d", email: "idk2@post.lol", phoneNumber: "054-lmao"}])

const mockSearchData = [{id: 123, label: 'idk (123)'}, {id: 1234, label: 'idk2 (1234)'}];

export default function SchoolsManagement(props){
    const [schools, setSchools] = useState([]);
    const [selectedSchoolSearchID, setSelectedSchoolSearchID] = useState('');
    const [searchText, setSearchText] = useState('');
    const [searchError, setSearchError] = useState(false);

    const [searching, setSearching] = useState(false);
    const [loaded, setLoaded] = useState(false);

    const [searchedSchoolDetails, setSearchedSchoolDetails] = useState({});


    // dialog states
    // add coordinator
    const [openACDialog, setOpenACDialog] = useState(false);
    const [selectedSchoolID, setSelectedSchoolID] = useState(-1);
    const [selectedSchoolName, setSelectedSchoolName] = useState('');

    // remove coordinator
    const [openDCDialog, setOpenDCDialog] = useState(false);
    const [selectedCoordinatorName, setSelectedCoordinatorName] = useState('');
    // TODO: what other info i need?



    // snackbar states
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [snackbarSeverity, setSnackbarSeverity] = useState('');
    const [snackbarMessage, setSnackbarMessage] = useState('');

    // STRINGS
    const page_title = "בתי הספר שלי";

    // search strings
    const search_school_label = 'חיפוש בית ספר';
    const search_error_string = "נא לבחור בית ספר";
    const search_button_string = "חיפוש";

    const school_id_cell_head_string = "סמל מוסד";
    const school_name_cell_head_string = "שם מוסד";
    const school_city_cell_head_string = "עיר מוסד";


    useEffect(() => {
        new Connection().getUserSchools(arrangeSchoolCallback)

        },[]);

    /**
     * arranges the school's data which got from the server
     * @param data the response from the server for the request to get the schools
     */
    const arrangeSchoolCallback = (data) => {
        if (!data.failure){

            data.result.forEach((element) => setSchools(schools => [...schools,
                {id: element.second, label: `${element.first} (${element.second})`}]))
            //
            //setSchools(data.result); // todo: this once i know how it's passed
        }
    }

    /**
     * arranges the school's data which got from the server
     * @param data the response from the server for the request to get the searched school
     */
    const getSchoolCallback = (data) => {
        if(!data.failure){
            setSearching(false);
            setLoaded(true);
        }
    }

    const submitSchoolSearch = () => {
        // checks if the current input matches any school
        const findInSchools = (label) => {
            return schools.find((ele) => ele.label === label) !== undefined
        }

        if(!findInSchools(searchText)){
            setSearchError(true);
        }
        else{
            setSearchError(false);
            setSearching(true);
            setLoaded(false);
            // TODO: get the selected school
        }
    }

    // add coordinator functions

    /**
     * handles the opening of the dialog to add a coordinator to a given school
     * @param schoolID the id of the selected school
     * @param schoolName the name of the selected school
     */
    const handleOpenAddCoordinatorDialog = (schoolID, schoolName) => {
        setOpenACDialog(true);
        setSelectedSchoolID(schoolID);
        setSelectedSchoolName(schoolName);
    }

    /**
     * an onClose handler for the AddCoordinator dialog
     */
    const handleCloseACDialog = () => {
        setOpenACDialog(false);
    }

    /**
     * a call back which handles the response from the server regarding the request to add a coordinator to a selected school
     * @param data the response from the server
     */
    const addCoordinatorCallback = (data) => {
        // TODO: implement
    }

    /**
     * an handler for adding a coordinator to a given school
     * @param workField the work field to which the coordinator is related to TODO: how do i get that?
     * @param firstName the first name of the coordinator to add
     * @param lastName the last name of the coordinator to add
     * @param email the email of the coordinator to add
     * @param phoneNumber the phone number of the coordinator to add
     * @param schoolID the school id of the school to which add the coordinator to
     */
    const handleAddCoordinator = (workField, firstName, lastName, email, phoneNumber, schoolID) => {
        new Connection().addCoordinator(
            workField,
            firstName,
            lastName,
            email,
            phoneNumber,
            schoolID,
            addCoordinatorCallback
        )
    }

    // remove coordinator functions

    /**
     * handles the opening of the dialog to remove a coordinator of a given school
     * @param coordinatorName the name of the coordinator to remove
     * @param schoolID the id of the school from which to remove the coordinator
     */
    const handleOpenRemoveCoordinatorDialog = (coordinatorName, schoolID) => {
        setOpenDCDialog(true);
        setSelectedCoordinatorName(coordinatorName);
        setSelectedSchoolID(schoolID)
    }

    /**
     * an onClose handler for the RemoveCoordinator dialog
     */
    const handleCloseDCDialog = () => {
        setOpenDCDialog(false);
    }

    /**
     * a call back which handles the response from the server regarding the request to remove a coordinator from a selected school
     * @param data the response from the server
     */
    const removeCoordinatorCallback = (data) => {
        // TODO: implement
    }

    /**
     * an handler for removing a coordinator of a given school
     */
    const handleRemoveCoordinator = (workField, schoolID) => {
        new Connection().removeCoordinator(
            workField,
            schoolID,
            removeCoordinatorCallback
        )
    }


    return (
        <Space.Fill scrollable>
            <div id="Schools-management">
                {/*title*/}
                <h1>{page_title}</h1>
                {/*the table presenting the schools*/}

                <Collapse in={searchError}>
                    <Alert severity={'error'} sx={{marginBottom: "2%"}}>{search_error_string}</Alert>
                </Collapse>


                <Autocomplete
                    freeSolo
                    id="search-schools"
                    disableClearable
                    onChange={(event, newValue) => {
                        setSelectedSchoolSearchID(newValue.id)
                    }}
                    onInputChange={(event, newInputValue) => {
                        setSearchText(newInputValue);
                    }}
                    options={schools}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            label={search_school_label}
                            onKeyDown={(e) => {
                                if(e.keyCode === 13){
                                    submitSchoolSearch()
                                }
                            }}
                            InputProps={{
                                ...params.InputProps,
                                type: 'search',
                                endAdornment: (
                                    <Button variant={"outlined"} onClick={() => submitSchoolSearch()}>{search_button_string}</Button>
                                )
                            }}
                            error={searchError}
                            sx={{marginBottom: "2%"}}
                        />
                    )}
                    sx={{width: "70%"}}
                />

                {searching ? (<Skeleton variant="rectangular" width={"70%"} height={500} />) : <div/>}

                {loaded ? <Row key={searchedSchoolDetails.id} row={searchedSchoolDetails} handleOpenAddCoordinatorDialog={handleOpenAddCoordinatorDialog}
                                handleOpenRemoveCoordinatorDialog={handleOpenRemoveCoordinatorDialog}/> : <div/>}

                {/*<TableContainer sx={{width: "70%", marginTop: "1%"}} component={Paper}>*/}
                {/*    <Table aria-label="collapsible table">*/}
                {/*        /!*the table head containing the various headers*!/*/}
                {/*        <TableHead>*/}
                {/*            <TableRow>*/}
                {/*                <TableCell/>*/}
                {/*                <TableCell>{school_id_cell_head_string}</TableCell>*/}
                {/*                <TableCell>{school_name_cell_head_string}</TableCell>*/}
                {/*                <TableCell>{school_city_cell_head_string}</TableCell>*/}
                {/*            </TableRow>*/}
                {/*        </TableHead>*/}
                {/*        /!*the body of the table containing the rows*!/*/}
                {/*        <TableBody>*/}
                {/*            {schools.map((tableRow) => (*/}
                {/*                <Row key={tableRow.id} row={tableRow} handleOpenAddCoordinatorDialog={handleOpenAddCoordinatorDialog}*/}
                {/*                handleOpenRemoveCoordinatorDialog={handleOpenRemoveCoordinatorDialog}/>*/}
                {/*            ))}*/}
                {/*        </TableBody>*/}
                {/*    </Table>*/}
                {/*</TableContainer>*/}

                {/*add coordinator dialog pop up*/}
                <AddCoordinatorDialog
                    schoolID={selectedSchoolID}
                    schoolName={selectedSchoolName}
                    open={openACDialog}
                    onClose={handleCloseACDialog}
                    callback={handleAddCoordinator}
                />

                {/*remove coordinator dialog pop up*/}
                <RemoveCoordinatorDialog
                    name={selectedCoordinatorName}
                    schoolID={selectedSchoolID}
                    open={openDCDialog}
                    onClose={handleCloseDCDialog}
                    callback={handleRemoveCoordinator}
                />

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