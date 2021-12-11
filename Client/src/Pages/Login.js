import React, { useState } from "react";
import './Login.css'
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import {FormControl, IconButton, InputAdornment, InputLabel, Input,} from "@mui/material";

export default function Login(){
    const [values, setValues] = useState({
        username: '',
        password: '',
        showPassword: false,
    });

    const handleChange = (prop) => (event) => {
        setValues({ ...values, [prop]: event.target.value });
    };

    const handleClickShowPassword = () => {
        setValues({
            ...values,
            showPassword: !values.showPassword,
        });
    };

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    return (
        <Box className="Login">
            <h1>מענ"ה</h1>
            <Box
                className="Login-form"
                component="form"
                sx={{
                    '& > :not(style)': { m: 1, width: '25ch' },
                }}
                noValidate
                autoComplete="off">

                <InputLabel dir='rtl' sx={{textAlign: 'right', display: 'block',
                    float: 'right', paddingBottom: '0'}}><b>שם משתמש:</b></InputLabel>
                <FormControl dir='rtl' sx={{ m: 1, width: '25ch', }} variant="standard">
                    <Input
                        type='text'
                        value={values.username}
                        onChange={handleChange('username')}
                    />
                </FormControl>


                <InputLabel dir='rtl' sx={{textAlign: 'right', display: 'block',
                    float: 'right', paddingBottom: '0'}}><b>סיסמה:</b></InputLabel>
                <FormControl dir='rtl' sx={{ m: 1, width: '25ch', }} variant="standard">
                    <Input
                        type={values.showPassword ? 'text' : 'password'}
                        value={values.password}
                        onChange={handleChange('password')}
                        endAdornment={
                            <InputAdornment position="start">
                                <IconButton
                                    aria-label="toggle password visibility"
                                    onClick={handleClickShowPassword}
                                    onMouseDown={handleMouseDownPassword}
                                >
                                    {values.showPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            </InputAdornment>
                        }
                    />
                </FormControl>
                {/*<FormControl variant="standard">*/}
                {/*    <InputLabel>סיסמה</InputLabel>*/}
                {/*    <OutlinedInput>*/}
                {/*        type={values.showPassword ? 'text' : 'password'}*/}
                {/*        value={values.password}*/}
                {/*        onChange={handleChange('password')}*/}
                {/*        endAdornment={*/}
                {/*        <InputAdornment position='end'>*/}
                {/*            <IconButton*/}
                {/*                onClick={handleClickShowPassword}*/}
                {/*                onMouseDown={handleMouseDownPassword}*/}
                {/*            >{values.showPassword ? <VisibilityOff/> : <Visibility/>}*/}
                {/*            </IconButton>*/}
                {/*        </InputAdornment>*/}
                {/*    }*/}
                {/*    label='password'*/}
                {/*    </OutlinedInput>*/}
                {/*</FormControl>*/}

                <Button variant="outlined">כניסה</Button>
            </Box>
            <Box>

            </Box>
        </Box>

    );
}