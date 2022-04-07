import * as Space from 'react-spaces';
import React, {useEffect, useState} from "react";
import Connection from "../../Communication/Connection";
import {Button, Grid, IconButton, InputAdornment, Tab, Tabs, TextField, Typography} from "@mui/material";

import InfoIcon from '@mui/icons-material/Info';
import LockIcon from '@mui/icons-material/Lock';
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";
import EditIcon from '@mui/icons-material/Edit';
import EditOffIcon from '@mui/icons-material/EditOff';

/**
 * tab viewer
 */
function TabPanel(props) {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`vertical-tabpanel-${index}`}
            aria-labelledby={`vertical-tab-${index}`}
            {...other}
        >
            {value === index && (
                    children
            )}
        </div>
    );
}

function a11yProps(index) {
    return {
        id: `vertical-tab-${index}`,
        'aria-controls': `vertical-tabpanel-${index}`,
    };
}

/**
 * containing the general information tab panel
 * @param props the properties passed to the element
 */
function InfoTabPanel(props){
    const [values, setValues] = useState({
        firstName: props.profileInfo.firstName,
        lastName: props.profileInfo.lastName,
        email: props.profileInfo.email,
        phoneNumber: props.profileInfo.phoneNumber,
        city: props.profileInfo.city
    });

    // tracks if there were changes to any of the values
    const [changes, setChanges] = useState(true);

    const [edit, setEdit] = useState(false);

    const first_name_label_string = "שם פרטי";
    const last_name_label_string = "שם משפחה";
    const email_label_string = "כתובת דואר אלקטרוני";
    const phone_number_label_string = "מספר טלפון";
    const city_label_string = "עיר";
    const save_button_string = "שמירה";

    const edit_string = 'ערכית פרטים';

    useEffect(() => {
        if (changes) {
            window.onbeforeunload = () => false
        } else {
            window.onbeforeunload = undefined
        }
    }, [])

    /**
     * handles the change of the text fields
     * @param prop the name of the value which has changed
     * @returns {(function(*): void)|*} function which saves the change
     */
    const handleChange = (prop) => (event) => {
        setValues({ ...values, [prop]: event.target.value });
        setChanges(true);
    }

    /**
     * a callback which handles the received data from the server regarding the save info request
     * @param data the message of the server regarding the request
     */
    const saveInfoCallback = (data) => {
        if (data.failure){
            props.setOpenSnackbar(true);
            props.setSnackbarSeverity('error');
            props.setSnackbarMessage('העדכון נכשל. אנא נסה/י שנית');
        }
        else {
            props.setOpenSnackbar(true);
            props.setSnackbarSeverity('success');
            props.setSnackbarMessage('הפרטים התעדכנו בהצלחה');
        }
    }

    /**
     * handler for saving the info data (sending the data to the server)
     */
    const saveInfoHandler = () => {
        new Connection().updateProfileInfo(
            values.firstName,
            values.lastName,
            values.email,
            values.phoneNumber,
            values.city,
            saveInfoCallback
        )
    }

    return (
        <Grid container spacing={2} rowSpacing={4} sx={{paddingTop: 1}}>
            {/*edit button*/}
            <Grid item xs={6}>
                <Button onClick={() => setEdit(!edit)} variant={edit ? "outlined" : "contained"} startIcon={edit? <EditOffIcon />  : <EditIcon />}>
                    {edit_string}
                </Button>
            </Grid>
            <Grid item xs={8}/>

            {/*first name*/}
            <Grid item xs={6}>
                <TextField
                    value={values.firstName}
                    onChange={handleChange('firstName')}
                    label={first_name_label_string}
                    inputProps={{
                        readOnly: !edit,
                    }}
                    fullWidth
                />
            </Grid>

            {/*last name*/}
            <Grid item xs={6}>
                <TextField
                    value={values.lastName}
                    onChange={handleChange('lastName')}
                    label={last_name_label_string}
                    inputProps={{
                        readOnly: !edit,
                    }}
                    fullWidth
                />
            </Grid>

            {/*email*/}
            <Grid item xs={12}>
                <TextField
                    value={values.email}
                    onChange={handleChange('email')}
                    label={email_label_string}
                    inputProps={{
                        readOnly: !edit,
                    }}
                    fullWidth
                />
            </Grid>

            {/*phone number*/}
            <Grid item xs={12}>
                <TextField
                    value={values.phoneNumber}
                    onChange={handleChange('phoneNumber')}
                    label={phone_number_label_string}
                    inputProps={{
                        readOnly: !edit,
                    }}
                    fullWidth
                />
            </Grid>

            {/*city*/}
            <Grid item xs={12}>
                <TextField
                    value={values.city}
                    onChange={handleChange('city')}
                    label={city_label_string}
                    inputProps={{
                        readOnly: !edit,
                    }}
                    fullWidth
                />
            </Grid>

            {/*save button*/}
            <Grid item xs={4}>
                <Button disabled={!edit} onClick={saveInfoHandler} variant="contained" fullWidth>{save_button_string}</Button>
            </Grid>
        </Grid>
    )
}

/**
 * containing the security tab panel
 * @param props the properties passed to the element
 */
function SecurityTabPanel(props){
    const [values, setValues] = useState({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: '',
    });

    const [showCurrentPassword, setShowCurrentPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showNewConfirmedPassword, setShowNewConfirmedPassword] = useState(false);

    const change_password_title_section_string = "שינוי סיסמה";
    const current_password_label_string = "סיסמה נוכחית";
    const new_password_label_string = "סיסמה חדשה";
    const confirm_new_password_label_string = "אימות סיסמה חדשה";
    const update_password_button_string = "עדכון סיסמה";

    /**
     * handles the change of the text fields
     * @param prop the name of the value which has changed
     * @returns {(function(*): void)|*} function which saves the change
     */
    const handleChange = (prop) => (event) => {
        setValues({ ...values, [prop]: event.target.value });
    }

    const updatePasswordCallback = (data) => {
        if(data.failure){
            // error
            props.setOpenSnackbar(true);
            props.setSnackbarSeverity('error');
            props.setSnackbarMessage('הפעולה נכשלה. אנא נסה/י שנית')
        }
        else{
            // success
            props.setOpenSnackbar(true);
            props.setSnackbarSeverity('success');
            props.setSnackbarMessage('הסיסמה שונתה בהצלחה');
        }

        // resetting the password fields no matter what
        setValues({
            currentPassword: '',
            newPassword: '',
            confirmNewPassword: '',
        })
    }

    /**
     * handles the update password action. sends a request to the server to change the password to the current user
     */
    const handleUpdatePassword = () => {
        new Connection().changePassword(
            values.currentPassword,
            values.newPassword,
            values.confirmNewPassword,
            updatePasswordCallback);
    }

    return (
        <Grid container spacing={2} rowSpacing={4} sx={{paddingTop: 1}}>
            {/*current password*/}
            <Grid item xs={3}><Typography variant="h6">{change_password_title_section_string}</Typography></Grid>
            <Grid item xs={12}>
                <TextField
                    value={values.currentPassword}
                    onChange={handleChange('currentPassword')}
                    label={current_password_label_string}
                    type={showCurrentPassword ? 'text' : 'password'}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton
                                    onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                                    onMouseDown={(event) => event.preventDefault()}
                                >
                                    {showCurrentPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                    fullWidth
                />
            </Grid>

            {/*new password*/}
            <Grid item xs={12}>
                <TextField
                    value={values.newPassword}
                    onChange={handleChange('newPassword')}
                    label={new_password_label_string}
                    type={showNewPassword ? 'text' : 'password'}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton
                                    onClick={() => setShowNewPassword(!showNewPassword)}
                                    onMouseDown={(event) => event.preventDefault()}
                                >
                                    {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                    fullWidth
                />
            </Grid>

            {/*confirm new password*/}
            <Grid item xs={12}>
                <TextField
                    value={values.confirmNewPassword}
                    onChange={handleChange('confirmNewPassword')}
                    label={confirm_new_password_label_string}
                    type={showNewConfirmedPassword ? 'text' : 'password'}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton
                                    onClick={() => setShowNewConfirmedPassword(!showNewConfirmedPassword)}
                                    onMouseDown={(event) => event.preventDefault()}
                                >
                                    {showNewConfirmedPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                    fullWidth
                />
            </Grid>

            {/*save button*/}
            <Grid item xs={4}>
                <Button onClick={handleUpdatePassword} variant="contained" fullWidth>{update_password_button_string}</Button>
            </Grid>
        </Grid>
    )
}

export default function ProfilePage(props){

    const [tabPage, setTabPage] = useState(0);
    const [loaded, setLoaded] = useState(false);
    const [profileInfo, setProfileInfo] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        city: ''
    });

    // snackbar
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [snackbarSeverity, setSnackbarSeverity] = useState('');
    const [snackbarMessage, setSnackbarMessage] = useState('');

    const page_title_string = "פרופיל";
    const info_tab_string = "פרטים";
    const security_tab_string = "אבטחה";

    useEffect(() => {
        new Connection().getProfileInfo(profileInfoCallback);
    }, []);

    const profileInfoCallback = (data) => {
        if (data.failure){
            // todo: raise error
        }
        else{
            setProfileInfo(data.result);
        }
        setLoaded(true);
    }

    /**
     * handles the change of a tab by updating the value which save the current open tab
     * @param event the event of the change
     * @param newValue the new tab value
     */
    const handleTabChange = (event, newValue) => {
        setTabPage(newValue);
    };

    return (
        <Space.Fill>

            {/*title of the page*/}
            <Space.Top size="10%">
                <Space.Centered>
                    <h1>{page_title_string}</h1>
                </Space.Centered>
            </Space.Top>

            {/*tabs selection*/}
            {loaded && <Space.Fill>
                <Space.Right size="10%">
                    <Tabs
                        orientation="vertical"
                        variant="scrollable"
                        value={tabPage}
                        onChange={handleTabChange}
                        aria-label="Vertical tabs example"
                        sx={{ borderRight: 1, borderColor: 'divider' }}
                    >
                        <Tab icon={<InfoIcon/>} iconPosition="start" label={info_tab_string} {...a11yProps(0)} />
                        <Tab icon={<LockIcon/>} iconPosition="start" label={security_tab_string} {...a11yProps(1)} />
                    </Tabs>
                </Space.Right>

                {/*tab panels*/}
                <Space.Fill>
                    <Space.Right size="30%"/>
                    <Space.Top size="10%"/>
                    <Space.Fill>
                        <Space.Top size="100%">

                                <TabPanel value={tabPage} index={0}>
                                    <InfoTabPanel profileInfo={profileInfo}
                                                  setOpenSnackbar={setOpenSnackbar}
                                                  setSnackbarSeverity={setSnackbarSeverity}
                                                  setSnackbarMessage={setSnackbarMessage}/>
                                </TabPanel>
                                <TabPanel value={tabPage} index={1}>
                                    <SecurityTabPanel setOpenSnackbar={setOpenSnackbar}
                                                      setSnackbarSeverity={setSnackbarSeverity}
                                                      setSnackbarMessage={setSnackbarMessage}/>
                                </TabPanel>
                        </Space.Top>
                    </Space.Fill>
                    <Space.Left size="30%"/>
                </Space.Fill>
            </Space.Fill>}

            {/*notification snackbar*/}
            <NotificationSnackbar
                open={openSnackbar}
                setOpen={setOpenSnackbar}
                severity={snackbarSeverity}
                message={snackbarMessage}/>
        </Space.Fill>
    )
}