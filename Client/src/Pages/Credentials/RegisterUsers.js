import React, { useState } from "react";
import './RegisterUsers.css'
import {Alert, FormHelperText, IconButton, InputAdornment, InputLabel, MenuItem, Paper, Select} from "@mui/material";
import TextField from "@mui/material/TextField";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import FormControl from "@mui/material/FormControl";

// TODO: random number just for it to work. check the real enums
const roles = [
    {
        roleEnum: 0,
        role: 'מדריכ/ה'
    },
    {
        roleEnum: 1,
        role: 'רכז/ת',
    },
    {
        roleEnum: 2,
        role: 'מפקח/ת',
    },
]

export default function RegisterUsers(){
    const [userInfo, setUserInfo] = useState({})
    const [showPassword, setShowPassword] = useState(false)
    const [showError, setShowError] = useState(false)
    const [errorMessage, setErrorMessage] = useState('');
    const [roleChoiceEnum, setRoleChoiceEnum] = useState(roles[0]['roleEnum']);
    const [roleChoice, setRoleChoice] = useState(roles[0]['role'])

    const header_string = 'רישום משתמשים'
    const username_label_string = 'שם משתמש'
    const password_label_string = 'סיסמה'
    const register_button_string = 'בצע/י רישום'
    const work_field_button_string = 'תחום'

    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        if(data.get('username') === '' || data.get('password') === '' || (roleChoice === 'מפקח/ת' && data.get('workField') === '')){
            setShowError(true);
            setErrorMessage('נא למלא את כל השדות')
        }
        else{
            setShowError(false);
        }

        // TODO: send the data
    }

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
                    <TextField
                        color="secondary"
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
                    <TextField
                        color="secondary"
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
                    <FormControl color="secondary" sx={{ m: 1, minWidth: 120 }}>
                        <InputLabel id="role-select-helper-label">תפקיד</InputLabel>
                        <Select
                            labelId="role-select-helper-label"
                            value={roleChoiceEnum}
                            label="תפקיד"
                            onChange={handleChange}
                        >
                            {roles.map(x => <MenuItem value={x['roleEnum']}>{x['role']}</MenuItem>)}
                        </Select>
                        <FormHelperText>תפקיד המשתמש הנרשם</FormHelperText>
                    </FormControl>
                    {roleChoice === 'מפקח/ת' &&
                        <TextField
                            color="secondary"
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