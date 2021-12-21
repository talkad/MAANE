import React, { useState, useEffect } from "react";
import './Login.css'
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import {Alert, FormHelperText, IconButton, InputAdornment, InputLabel, MenuItem, Paper, Select,} from "@mui/material";
import FormControl from "@mui/material/FormControl";
import Connection from "../../Communication/Connection";

export default function Login(){
    const [showPassword, setShowPassword] = useState(false);
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [loaded, setLoaded] = useState(false);

    const header_string = 'מענ"ה'
    const username_label_string  = "שם משתמש"
    const password_label_string  = "סיסמה"
    const login_button_string  = "כניסה"

    useEffect(() => {
        const callback = function(response) {
            var str = '';
          
            //another chunk of data has been received, so append it to `str`
            response.on('data', function (chunk) {
              str += chunk;
            });
          
            //the whole response has been received, so we just print it out here
            response.on('end', function () {
                Connection.setUsername(str);
            });

            // response.on('error', function () {
            //     console.log("got rejected :((((. like always :`(")
            // })

            setLoaded(true);
          }

          Connection.setUpUser(callback);
      }, []);


    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        if(data.get('username') === '' || data.get('password') === ''){
            setShowError(true);
            setErrorMessage('נא למלא את כל השדות')
        }
        else{
            setShowError(false);
        }

        // TODO: send the data
    }

    return (
        <div className="Login">
            {!loaded ? <h1>wait ffs</h1> : <div>
                <h1>{header_string}</h1>
                <Paper className="Login-paper" elevation={3}>
                    <Box className="Login-form" component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1, }}>
                        <TextField
                            color="secondary"
                            error={showError}
                            margin="normal"
                            variant="standard"
                            required
                            fullWidth
                            id="username"
                            label={username_label_string }
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
                        {showError && <Alert severity="error">{errorMessage}</Alert>}
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