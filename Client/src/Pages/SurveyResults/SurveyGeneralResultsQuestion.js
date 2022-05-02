import React, { useState } from "react";
import {
    Box,
    FormControl,
    FormControlLabel,
    FormHelperText,
    FormLabel,
    Grid, LinearProgress, linearProgressClasses,
    Paper,
    Radio,
    RadioGroup,
    Typography
} from "@mui/material";
import TextField from "@mui/material/TextField";
import {styled} from "@mui/styles";

function LinearProgressWithLabel(props) {
    const BorderLinearProgress = styled(LinearProgress)(({ theme }) => ({
        height: 20,
        borderRadius: 5,
        [`&.${linearProgressClasses.colorPrimary}`]: {
            backgroundColor: theme.palette.grey[theme.palette.mode === 'light' ? 200 : 800],
        },
        [`& .${linearProgressClasses.bar}`]: {
            borderRadius: 5,
            backgroundColor: theme.palette.mode === 'light' ? '#1a90ff' : '#308fe8',
        },
    }));

    return (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Box sx={{ width: '100%', mr: 1 }}>
                <BorderLinearProgress variant="determinate" {...props} />
            </Box>
            <Box sx={{ minWidth: 35 }}>
                <Typography variant="body2" color="text.secondary">{`${Math.round(
                    props.value,
                )}%`}</Typography>
            </Box>
        </Box>
    );
}

export default function SurveyGeneralResultsQuestion(props){

    // STRINGS
    const answer_label_string = 'תשובה';
    const numeral_answer_label_string = 'תשובה מספרית'
    const open_answer_helper_text_string = 'נא להזין תשובה מספרית בלבד'

    const answer_average_text_string = "ממוצע התשובות לשאלה זו הוא:";




    return (
        <div className="SurveyQuestion">
            <Paper className="Survey-paper" elevation={3}>
                <Grid container spacing={2}>
                    <Grid item xs={9}>
                        {/*the question*/}
                        <TextField
                            color="secondary"
                            sx={{width: "100%"}}
                            margin="normal"
                            variant="filled"
                            name="question"
                            defaultValue={props.questionString}
                            InputProps={{
                                readOnly: true,
                            }}
                        />
                    </Grid>
                    {/*multiple-choice view for question of this kind*/}
                    {props.type === 'MULTIPLE_CHOICE' &&
                        <Grid item xs={12} sx={{margin: "1%", width: "100%"}}>
                            <FormControl variant="standard" sx={{width: "100%"}}>
                                {/*<FormLabel>{multiple_choice_label_string}</FormLabel>*/}
                                <RadioGroup
                                >
                                    {props.choices.map((element, index) =>
                                        <Grid container spacing={1}>
                                            <Grid item sx={{alignItems: 'center'}} xs={2}><FormControlLabel value={index.toString()} control={<Radio color="secondary"/>}
                                                                                label={<Typography>{element}</Typography>}/></Grid>
                                            <Grid item xs={5}>
                                                <LinearProgressWithLabel variant="determinate" value={64} />
                                            </Grid>
                                        </Grid>
                                        )}
                                </RadioGroup>
                            </FormControl>
                        </Grid>

                    }

                    {/*open-numerical view for question of this kind*/}
                    {props.type === 'NUMERIC_ANSWER'  && <Grid sx={{alignItems: 'center', margin: "1%"}} item xs={12}>
                        <Typography display={"inline"}>{answer_average_text_string} </Typography>
                        <Typography display={"inline"} color={'blue'}>{props.statistics}</Typography>
                    </Grid>}
                </Grid>
            </Paper>
        </div>
    )
}