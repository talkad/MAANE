import React, { useState, useEffect } from "react";
import './Login.css'
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import {Alert, IconButton, InputAdornment, Paper,} from "@mui/material";
import Connection from "../../Communication/Connection";
import { useNavigate } from 'react-router-dom'

export default function Login(props){
    const [showPassword, setShowPassword] = useState(false);
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [loaded, setLoaded] = useState(false);

    let navigate = useNavigate();

    // STRINGS
    const header_string = 'מענ"ה'
    const username_label_string  = "שם משתמש"
    const password_label_string  = "סיסמה"
    const login_button_string  = "כניסה"

    /**
     * before the page loads, sends a request to the server to set up the user (as guest) and handles it
     */
    useEffect(() => {
        const callback = function(data) {

            //UserInfo.getInstance().setUsername(data.result);
            window.sessionStorage.setItem('username', data.result);
            setLoaded(true);
          }

          Connection.getInstance().setUpUser(callback);
      }, []);

    /**
     * a callback handler for the log in request which logs in the user on success and raises an error on failure
     * @param data
     */
    const loginCallback = (data) => {
        if(data.failure){
            setShowError(true);
            setErrorMessage('שם משתמש או סיסמה לא נכונים');
        }
        else{
            //UserInfo.getInstance().setUsername(username)
            window.sessionStorage.setItem('username', data.result.first);
            //UserInfo.getInstance().setType(type);
            props.changeType(data.result.second);
            props.setHideBars(false);
            //window.sessionStorage.setItem('type', type);

            navigate(`../home`, {replace: false}) // TODO: check what does the replace mean
        }

    }

    /**
     * gets the username and password data from the components and sends a log in request to the server
     * @param event wrapper for the input data
     */
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        if(data.get('username') === '' || data.get('password') === ''){
            setShowError(true);
            setErrorMessage('נא למלא את כל השדות');
        }
        else{
            setShowError(false);
            Connection.getInstance().login(
                window.sessionStorage.getItem('username'),
                data.get('username'),
                data.get('password'),
                loginCallback);
        }

    }

    return (
        <div className="Login">
            {loaded && <div>
                <h1>{header_string}</h1>
                <Paper className="Login-paper" elevation={3}>
                    <Box className="Login-form" component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1, }}>
                        {/* username */}
                        <TextField
                            color="secondary"
                            error={showError}
                            margin="normal"
                            variant="standard"
                            required
                            fullWidth
                            id="username"
                            label={username_label_string}
                            name="username"
                            autoComplete="username"
                            autoFocus
                        />
                        {/* password */}
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
                        {showError && <Alert severity="error">{errorMessage}</Alert>}
                        {/* submit login */}
                        <Button
                            color="secondary"
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            {login_button_string}
                        </Button>
                    </Box>
                </Paper>
            </div>}
        </div>
    );
}