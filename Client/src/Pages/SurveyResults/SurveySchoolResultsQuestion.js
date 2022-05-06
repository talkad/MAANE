import React, { useState } from "react";
import {
    FormControl,
    FormControlLabel,
    FormHelperText,
    FormLabel,
    Grid,
    Paper,
    Radio,
    RadioGroup,
    Typography
} from "@mui/material";
import TextField from "@mui/material/TextField";

export default function surveySchoolResultsQuestion(props){

    // STRINGS
    const answer_label_string = 'תשובה';
    const numeral_answer_label_string = 'תשובה מספרית'
    const open_answer_helper_text_string = 'נא להזין תשובה מספרית בלבד'

    const multiple_choice_label_string = "נא לבחור תשובה אחת:";
    const multiple_choice_helper_text_string = "יש לבחור תשובה";

    const violation_of_goals_string = 'תשובה זו מפרה את היעדים הבאים שהוצבו';

    /**
     * onChange callback when a there a new selection for multiple-choice question's answer
     * @param event
     */
    const handleMultipleChoiceAnswerChange = (event) => {
        //setMultipleChoiceAnswer(event.target.value);
        props.answerChange(props.id, event.target.value)
    }


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
                        <Grid item xs={12} sx={{margin: "1%"}}>
                            <FormControl error={!props.isLegal} variant="standard">
                                <FormLabel>{multiple_choice_label_string}</FormLabel>
                                <RadioGroup
                                    value={props.answer}
                                    onChange={handleMultipleChoiceAnswerChange}
                                >
                                    {props.choices.map((element, index) =>
                                        <FormControlLabel value={index.toString()} control={<Radio color="secondary"/>} label={element}/>)}
                                </RadioGroup>
                                {!props.isLegal && <FormHelperText><Typography display={'inline'}><Typography display={'inline'}>props.violatedGoals.reduce((pv, cv) => `${pv}, ${cv}`, '')</Typography> {violation_of_goals_string}</Typography></FormHelperText>}
                            </FormControl>
                        </Grid>

                    }
                    {/*open view for question of this kind*/}
                    {props.type === 'OPEN_ANSWER' && <Grid sx={{alignItems: 'center', margin: "1%"}} item xs={12}>
                        <TextField
                            color="secondary"
                            sx={{width: "90%"}}
                            margin="normal"
                            variant="standard"
                            value={props.answer}
                            error={!props.isLegal}
                            helperText={!props.isLegal ? <Typography display={'inline'}><Typography display={'inline'}>props.violatedGoals.reduce((pv, cv) => `${pv}, ${cv}`, '')</Typography> {violation_of_goals_string}</Typography> : ''}
                            disabled
                        />
                    </Grid>}
                    {/*open-numerical view for question of this kind*/}
                    {props.type === 'NUMERIC_ANSWER'  && <Grid sx={{alignItems: 'center', margin: "1%"}} item xs={12}>
                        <TextField
                            color="secondary"
                            sx={{width: "90%"}}
                            margin="normal"
                            variant="standard"
                            disabled
                            error={!props.isLegal}
                            helperText={!props.isLegal ? <Typography display={'inline'}><Typography display={'inline'}>props.violatedGoals.reduce((pv, cv) => `${pv}, ${cv}`, '')</Typography> {violation_of_goals_string}</Typography> : ''}
                            inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                            value={props.answer}
                        />
                    </Grid>}
                </Grid>
            </Paper>
        </div>
    )
}