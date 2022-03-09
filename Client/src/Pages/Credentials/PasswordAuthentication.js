import React, { useState, useEffect } from "react";
import * as Space from 'react-spaces';
import {Button, Card, CardContent, IconButton, InputAdornment, Paper, Stack, TextField} from "@mui/material";
import Box from "@mui/material/Box";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";


// todo: make this screen get the previous and next pages to go to

export default function PasswordAuthentication(props){
    const [showPassword, setShowPassword] = useState(false);

    const auth_password_string = "אימות סיסמה"
    const explanation_string = 'בבקשה הכניס/י את הסיסמה שלך לצורך אימות'
    const password_string = "סיסמה"
    const auth_string = "אימות"

    useEffect(() => {
        props.setHideBars(true);
    });

    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        // todo: send auth
    }

    return (
        <Space.Top size="40%">
            <Space.Centered>
                {/*the authentication paper*/}
                <Paper elevation={1} sx={{marginLeft: "25%", marginRight: "25%"}}>
                    <h2>{auth_password_string}</h2>
                    <p>{explanation_string}</p>
                    <Stack component="form" sx={{alignItems: "center"}} onSubmit={handleSubmit}>
                        <TextField name="password"
                                   sx={{paddingBottom: 1, width: "50%"}}
                                   id="outlined-basic"
                                   label={password_string}
                                   variant="outlined"
                                   type={showPassword ? 'text' : 'password'}
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
                                   }}/>
                        <Button type="submit" sx={{marginBottom: 1, width: "20%"}} variant="outlined">{auth_string}</Button>
                    </Stack>
                </Paper>
            </Space.Centered>
        </Space.Top>
    )
}