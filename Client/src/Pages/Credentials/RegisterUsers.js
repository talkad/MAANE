import React, {useEffect, useState} from "react";
import './RegisterUsers.css'
import {Alert, FormHelperText, IconButton, InputAdornment, InputLabel, MenuItem, Paper, Select} from "@mui/material";
import TextField from "@mui/material/TextField";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import FormControl from "@mui/material/FormControl";
import Connection from "../../Communication/Connection";

// TODO: change to react-space


// for offline testing
const roles_system_manager = [
    {
        roleEnum: 0,
        role: 'מדריכ/ה'
    },
    {
        roleEnum: 1,
        role: 'מפקח/ת כללי/ת',
    },
    {
        roleEnum: 2,
        role: 'מפקח/ת',
    },
]

const roles_supervisor = [
    {
        roleEnum: 0,
        role: 'מדריכ/ה'
    },
    {
        roleEnum: 1,
        role: 'מפקח/ת כללי/ת',
    },
]

export default function RegisterUsers(props){
    const [userInfo, setUserInfo] = useState({})
    const [values, setValues] = useState({
        username: '',
        password: '',
        workField: '',
    })
    const [showPassword, setShowPassword] = useState(false)
    const [showError, setShowError] = useState(false)
    const [errorMessage, setErrorMessage] = useState('');
    const [roleChoiceEnum, setRoleChoiceEnum] = useState(-1);
    const [roleChoice, setRoleChoice] = useState('')
    const [roles, setRoles] = useState([])

    // STRINGS
    const header_string = 'רישום משתמשים';
    const username_label_string = 'שם משתמש';
    const password_label_string = 'סיסמה';
    const register_button_string = 'בצע/י רישום';
    const work_field_button_string = 'תחום';
    const select_label_string = 'תפקיד';
    const select_helper_text_string = 'תפקיד המשתמש הנרשם';
    const supervisor_string = 'מפקח/ת'

    /**
     * before the screen loads sets the relevant components according to the type of user who got to the screen
     */
    useEffect(() => {
        let selected_roles = props.type === 'SUPERVISOR' ? roles_supervisor : roles_system_manager
        setRoles(selected_roles)
        setRoleChoiceEnum(selected_roles[0]['ruleEnum'])
        setRoleChoice(selected_roles[0]['role'])
    },[props.type]);

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
            // TODO: currently the functionality doesn't work correctly so check on this later
            console.log(data)
        }
    }

    /**
     * on submitting to register a new user, checks all the fields and not empty and sends request to the server to
     * register a new user with the given input
     * @param event wrapper for the state of the components at the moment of the submit
     */
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        if(data.get('username') === '' || data.get('password') === '' || (roleChoice === 'מפקח/ת' && data.get('workField') === '')){
            setShowError(true);
            setErrorMessage('נא למלא את כל השדות')
        }
        else{
            // TODO: change this once there is an option to register a supervisor
            setShowError(false);
            Connection.getInstance().register(
                    window.sessionStorage.getItem('username'),
                    data.get('username'),
                    data.get('password'),
                    roleChoiceEnum,
                registerCallback);
        }
    }

    /**
     * onChange handler for the dropdown list of roles
     * @param event wrapper for the changed component
     */
    const handleChange = (event) => {
        setRoleChoiceEnum(event.target.value);
        const role = roles.find(x => x["roleEnum"] === event.target.value)
        setRoleChoice(role["role"])
    };

    return (
        <div className="Register-users">
            <h1>{header_string}</h1>
            <Paper className="Register-users-paper" elevation={3}>
                <Box className="Register-users-form" component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1, }}>
                    {/* username text field */}
                    <TextField
                        color="secondary"
                        value={values.username}
                        onChange={handleTextFieldsChange('username')}
                        error={showError}
                        margin="normal"
                        variant="standard"
                        required
                        fullWidth
                        id="username"
                        label={ username_label_string }
                        name="username"
                        autoComplete="username"
                        autoFocus
                    />
                    {/* password text field */}
                    <TextField
                        color="secondary"
                        value={values.password}
                        onChange={handleTextFieldsChange('password')}
                        error={showError}
                        margin="normal"
                        variant="standard"
                        required
                        fullWidth
                        name="password"
                        label={password_label_string}
                        type={showPassword ? 'text' : 'password'}
                        id="password"
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
                    {/* role choice form */}
                    <FormControl color="secondary" sx={{ m: 1, minWidth: 120 }}>
                        <InputLabel id="role-select-helper-label">תפקיד</InputLabel>
                        <Select
                            labelId="role-select-helper-label"
                            value={roleChoiceEnum}
                            label={select_label_string}
                            onChange={handleChange}
                        >
                            {roles.map(x => <MenuItem value={x['roleEnum']}>{x['role']}</MenuItem>)}
                        </Select>
                        <FormHelperText>{select_helper_text_string}</FormHelperText>
                    </FormControl>
                    {/*optional text-field if the user chose to register a supervisor*/}
                    {roleChoice === supervisor_string &&
                        <TextField
                            color="secondary"
                            value={values.workField}
                            onChange={handleTextFieldsChange('workField')}
                            error={showError}
                            margin="normal"
                            variant="standard"
                            required
                            fullWidth
                            id="workField"
                            label={ work_field_button_string }
                            name="workField"
                        />}
                    {showError && <Alert severity="error">{errorMessage}</Alert>}
                    {/* submit register button */}
                    <Button
                        color="secondary"
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        {register_button_string}
                    </Button>
                </Box>
            </Paper>
        </div>
    )
}