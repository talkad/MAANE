import React, {useEffect, useState} from "react";
import './RegisterUsers.css'
import {
    Alert,
    FormHelperText,
    Grid,
    IconButton,
    InputAdornment,
    InputLabel,
    MenuItem,
    Paper,
    Select, Typography
} from "@mui/material";
import TextField from "@mui/material/TextField";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import Connection from "../../Communication/Connection";
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";
import {useNavigate} from "react-router-dom";
import * as Space from 'react-spaces';

// for offline testing
const roles_system_manager = [
    {
        roleEnum: "INSTRUCTOR",
        role: 'מדריכ/ה'
    },
    {
        roleEnum: "GENERAL_SUPERVISOR",
        role: 'מפקח/ת כללי/ת',
    },
    {
        roleEnum: "SUPERVISOR",
        role: 'מפקח/ת',
    },
]

const roles_supervisor = [
    {
        roleEnum: "INSTRUCTOR",
        role: 'מדריכ/ה'
    },
    {
        roleEnum: "GENERAL_SUPERVISOR",
        role: 'מפקח/ת כללי/ת',
    },
]

// TODO: for some reason a supervisor becomes an admin after something is happening. can't tell what atm

export default function RegisterUsers(props){
    // data
    const [values, setValues] = useState({
        username: '',
        password: '',
        workField: '',
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        city: '',
    });

    // password
    const [showPassword, setShowPassword] = useState(false);

    // error
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    // role choice
    const [roleChoiceEnum, setRoleChoiceEnum] = useState('');

    // supervisor choice
    const [supervisorChoiceUsername, setSupervisorChoiceUsername] = useState('');

    // select options
    const [roles, setRoles] = useState([]);
    const [supervisors, setSupervisors] = useState([]);

    // notification snackbar
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [snackbarSeverity, setSnackbarSeverity] = useState('');
    const [snackbarMessage, setSnackbarMessage] = useState('');

    let navigate = useNavigate();

    // STRINGS
    const header_string = 'רישום משתמשים';

    const user_credentials_string = 'פרטי התחברות:';
    const user_info_string = 'פרטים אישיים:';
    const user_assign_info = 'הקצאות:'

    const username_label_string = 'שם משתמש';
    const password_label_string = 'סיסמה';
    const first_name_label_string = 'שם פרטי';
    const last_name_label_string = 'שם משפחה';
    const email_label_string = 'דוא"ל';
    const phone_number_label_string = 'מספר טלפון';
    const city_label_string = 'עיר מגורים';

    const register_button_string = 'בצע/י רישום';
    const finished_button_string = 'סיום';

    const work_field_button_string = 'תחום';
    const select_role_label_string = 'תפקיד';

    const select_supervisor_label_string = 'מפקח/ת';
    const select_role_helper_text_string = 'תפקיד המשתמש הנרשם';
    const select_supervisor_helper_text_string = 'המפקח/ת שתחתיו/ה המשתמש יוגדר';

    /**
     * before the screen loads sets the relevant components according to the type of user who got to the screen
     */
    useEffect(() => {
        let selected_roles = props.type === 'SUPERVISOR' ? roles_supervisor : roles_system_manager;
        setRoles(selected_roles);
        setRoleChoiceEnum(selected_roles[0]['ruleEnum']);

        if (props.type === "SYSTEM_MANAGER"){
            Connection.getInstance().getSupervisors(window.sessionStorage.getItem('username'), arrangeSupervisorsCallback)
        }
    },[]);

    const refresh_supervisors = () => {
        Connection.getInstance().getSupervisors(window.sessionStorage.getItem('username'), arrangeSupervisorsCallback)
    }

    const arrangeSupervisorsCallback = (data) => {
        if (data.failure){
            // todo: needed?
        }
        else {
            setSupervisors(data.result);
        }
    }

    /**
     * onChange handler for the text-fields
     * @param props the field who's been changed
     * @returns {(function(*): void)|*} updating the new values in the state hook
     */
    const handleTextFieldsChange = (props) => (event) => {
        setValues({ ...values, [props]: event.target.value})
    }

    /**
     * callback function for the response of the server for the register request
     * @param data
     */
    const registerCallback = (data) => {
        if(data.failure){
            setShowError(true);
            setErrorMessage('ההרשמה נדחתה');
        }
        else{
           setValues({
               username: '',
               password: '',
               workField: '',
               firstName: '',
               lastName: '',
               email: '',
               phoneNumber: '',
               city: '',
           });
           setRoleChoiceEnum('');
           setOpenSnackbar(true);
           setSnackbarSeverity('success');
           setSnackbarMessage('המשתמש נרשם בהצלחה');
        }
    }

    /**
     * on submitting to register a new user, checks all the fields and not empty and sends request to the server to
     * register a new user with the given input
     * @param event wrapper for the state of the components at the moment of the submit
     */
    const handleSubmit = (event) => {
        event.preventDefault();

        if(values.username === '' || values.password === '' || roleChoiceEnum === undefined || roleChoiceEnum === '' ||
            (roleChoiceEnum === 'SUPERVISOR' && values.workField === '') ||
            (props.type === "SYSTEM_MANAGER" && (roleChoiceEnum === "INSTRUCTOR" || roleChoiceEnum === "GENERAL_SUPERVISOR") && supervisorChoiceUsername === '')){

            setShowError(true);
            setErrorMessage('נא למלא את כל שדות החובה')
        }
        else{
            setShowError(false);

            if (props.type === 'SUPERVISOR'){
                Connection.getInstance().registerUser(
                    window.sessionStorage.getItem('username'),
                    values.username,
                    values.password,
                    roleChoiceEnum,
                    values.firstName,
                    values.lastName,
                    values.email,
                    values.phoneNumber,
                    values.city,
                    registerCallback);
            }

            if (props.type === 'SYSTEM_MANAGER'){
                Connection.getInstance().registerUserBySystemManager(
                    window.sessionStorage.getItem('username'),
                    values.username,
                    values.password,
                    roleChoiceEnum,
                    values.firstName,
                    values.lastName,
                    values.email,
                    values.phoneNumber,
                    values.city,
                    values.workField,
                    supervisorChoiceUsername,
                    registerCallback
                );

                if (roleChoiceEnum === "SUPERVISOR") {
                    refresh_supervisors();
                }
            }
        }
    }

    /**
     * onChange handler for the dropdown list of roles
     * @param event wrapper for the changed component
     */
    const handleRoleChoiceChange = (event) => {
        setRoleChoiceEnum(event.target.value);
    };

    /**
     * onChange handler for the dropdown list of supervisors
     * @param event wrapper for the changed component
     */
    const handleSupervisorChoiceChange = (event) => {
        setSupervisorChoiceUsername(event.target.value);
    }

    return (
        <Space.Fill scrollable>
            <div className="Register-users">
                <h1>{header_string}</h1>
                <Paper className="Register-users-paper" sx={{width: "25%"}} elevation={3}>
                    <Grid container spacing={1} >

                        {/*user credentials sub-title*/}
                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%", marginTop: "3%"}}>
                            <Typography variant="h7">{user_credentials_string}</Typography>
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/* username text field */}
                            <TextField
                                color="secondary"
                                value={values.username}
                                onChange={handleTextFieldsChange('username')}
                                error={showError}
                                margin="normal"
                                variant="outlined"
                                required
                                fullWidth
                                label={ username_label_string }
                                name="username"
                                autoComplete="username"
                                autoFocus
                            />
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/* password text field */}
                            <TextField
                                color="secondary"
                                value={values.password}
                                onChange={handleTextFieldsChange('password')}
                                error={showError}
                                margin="normal"
                                variant="outlined"
                                required
                                fullWidth
                                name="password"
                                label={password_label_string}
                                type={showPassword ? 'text' : 'password'}
                                autoComplete="current-password"
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
                                }}
                            />
                        </Grid>

                        {/*user into sub-title*/}
                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%", marginTop: "3%"}}>
                            <Typography variant="h7">{user_info_string}</Typography>
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/*additional information fields*/}
                            <Grid container spacing={1}>
                                {/*first name*/}
                                <Grid item xs={6}>
                                    <TextField
                                        color="secondary"
                                        variant="outlined"
                                        margin="normal"
                                        label={first_name_label_string}
                                        value={values.firstName}
                                        onChange={handleTextFieldsChange('firstName')}
                                        error={showError}
                                        fullWidth
                                        required
                                    />
                                </Grid>

                                {/*last name*/}
                                <Grid item xs={6}>
                                    <TextField
                                        color="secondary"
                                        variant="outlined"
                                        margin="normal"
                                        label={last_name_label_string}
                                        value={values.lastName}
                                        onChange={handleTextFieldsChange('lastName')}
                                        error={showError}
                                        fullWidth
                                        required
                                    />
                                </Grid>

                                {/*email name*/}
                                <Grid item xs={12}>
                                    <TextField
                                        color="secondary"
                                        variant="outlined"
                                        margin="normal"
                                        label={email_label_string}
                                        value={values.email}
                                        onChange={handleTextFieldsChange('email')}
                                        fullWidth
                                    />
                                </Grid>

                                {/*phone number name*/}
                                <Grid item xs={12}>
                                    <TextField
                                        color="secondary"
                                        variant="outlined"
                                        margin="normal"
                                        label={phone_number_label_string}
                                        value={values.phoneNumber}
                                        onChange={handleTextFieldsChange('phoneNumber')}
                                        fullWidth
                                    />
                                </Grid>

                                {/*city name*/}
                                <Grid item xs={12}>
                                    <TextField
                                        color="secondary"
                                        variant="outlined"
                                        margin="normal"
                                        label={city_label_string}
                                        value={values.city}
                                        onChange={handleTextFieldsChange('city')}
                                        fullWidth
                                    />
                                </Grid>
                            </Grid>
                        </Grid>

                        {/*user into sub-title*/}
                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%", marginTop: "3%"}}>
                            <Typography variant="h7">{user_assign_info}</Typography>
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/* role choice form */}
                            <FormControl color="secondary" sx={{ m: 1, minWidth: 120 }}>
                                <InputLabel id="role-select-helper-label">{select_role_label_string}</InputLabel>
                                <Select
                                    labelId="role-select-helper-label"
                                    value={roleChoiceEnum}
                                    label={select_role_label_string}
                                    onChange={handleRoleChoiceChange}
                                >
                                    {roles.map(x => <MenuItem value={x['roleEnum']}>{x['role']}</MenuItem>)}
                                </Select>
                                <FormHelperText>{select_role_helper_text_string}</FormHelperText>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/*optional select if the user is admin and want to register a non-supervisor user under an existing supervisor*/}
                            {props.type === 'SYSTEM_MANAGER' && (roleChoiceEnum === "INSTRUCTOR" || roleChoiceEnum === "GENERAL_SUPERVISOR") &&
                                <FormControl color="secondary" sx={{ m: 1, minWidth: 120 }}>
                                    <InputLabel id="supervisor-select-helper-label">{select_supervisor_label_string}</InputLabel>
                                    <Select
                                        labelId="supervisor-select-helper-label"
                                        value={supervisorChoiceUsername}
                                        label={select_supervisor_label_string}
                                        onChange={handleSupervisorChoiceChange}
                                    >
                                        {supervisors.map(x => <MenuItem value={x['currUser']}>{x['firstName'] + " " + x['lastName'] + "בתחום " + x['workField']}</MenuItem>)}
                                    </Select>
                                    <FormHelperText>{select_supervisor_helper_text_string}</FormHelperText>
                                </FormControl>}
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/*optional text-field if the user chose to register a supervisor*/}
                            {roleChoiceEnum === "SUPERVISOR" &&
                                <TextField
                                    color="secondary"
                                    value={values.workField}
                                    onChange={handleTextFieldsChange('workField')}
                                    error={showError}
                                    margin="normal"
                                    variant="outlined"
                                    required
                                    fullWidth
                                    label={ work_field_button_string }
                                />}
                        </Grid>

                        <Grid item xs={12} sx={{marginRight: "3%", marginLeft: "3%"}}>
                            {/*alert for errors*/}
                            {showError && <Alert severity="error">{errorMessage}</Alert>}
                        </Grid>

                        <Grid item xs={4}/>
                        <Grid item xs={8}>
                            {/* submit register button */}
                            <Button
                                color="secondary"
                                type="submit"
                                fullWidth
                                variant="contained"
                                onClick={handleSubmit}
                                sx={{ mt: 3, mb: 2, width: "50%" }}
                            >
                                {register_button_string}
                            </Button>
                        </Grid>

                        <Grid item xs={4}/>
                        <Grid item xs={8}>
                            <Button sx={{marginBottom: 1, width: "50%"}} color='success' fullWidth variant="contained" onClick={() => navigate(-1)}>{finished_button_string}</Button>
                        </Grid>

                    </Grid>
                </Paper>
                <NotificationSnackbar
                    open={openSnackbar}
                    setOpen={setOpenSnackbar}
                    severity={snackbarSeverity}
                    message={snackbarMessage}/>
            </div>
        </Space.Fill>
    )
}