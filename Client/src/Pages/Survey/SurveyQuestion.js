import React, { useState } from "react";
import {FormControlLabel, Grid, Paper, Radio, RadioGroup} from "@mui/material";
import TextField from "@mui/material/TextField";

export default function SurveyQuestion(props){
    const [openAnswer, setOpenAnswer] = useState('');
    const [multipleChoiceAnswer, setMultipleChoiceAnswer] = useState(0);

    // STRINGS
    const answer_label_string = 'תשובה';
    const numeral_answer_label_string = 'תשובה מספרית'
    const open_answer_helper_text_string = 'נא להזין תשובה מספרית בלבד'

    /**
     * onChange callback when there a change to the answer text-field
     * @param event the changed element
     */
    const handleOpenAnswerChange = (event) => {
        setOpenAnswer(event.target.value)
        props.answerChange(props.id, event.target.value)
    }

    /**
     * onChange callback when a there a new selection for multiple-choice question's answer
     * @param event
     */
    const handleMultipleChoiceAnswerChange = (event) => {
        setMultipleChoiceAnswer(event.target.value);
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
                            className="SurveyQuestion-text-field"
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
                    {props.type === 'multiple' &&
                        <Grid item xs={12}>
                                <RadioGroup
                                    aria-label="gender"
                                    name="controlled-radio-buttons-group"
                                    value={multipleChoiceAnswer}
                                    onChange={handleMultipleChoiceAnswerChange}
                                >
                                    {props.answers.map((element, index) =>
                                        <FormControlLabel value={index} control={<Radio color="secondary"/>} label={element}/>)}
                                </RadioGroup>
                        </Grid>


                    }
                    {/*open view for question of this kind*/}
                    {props.type === 'open' && <Grid sx={{alignItems: 'center'}} item xs={12}>
                        <TextField
                            color="secondary"
                            className="SurveyQuestion-text-field"
                            margin="normal"
                            variant="standard"
                            required
                            value={openAnswer}
                            label={answer_label_string}
                            onChange={handleOpenAnswerChange}
                        />
                    </Grid>}
                    {/*TODO: see about this thingy down below*/}
                    {/*open-numerical view for question of this kind*/}
                    {props.type === 'open-number'  && <Grid sx={{alignItems: 'center'}} item xs={12}>
                        <TextField
                            color="secondary"
                            className="SurveyQuestion-text-field"
                            margin="normal"
                            variant="standard"
                            required
                            type="number"
                            inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                            value={openAnswer}
                            helperText={open_answer_helper_text_string}
                            label={numeral_answer_label_string}
                            onChange={handleOpenAnswerChange}
                        />
                    </Grid>}
                </Grid>
            </Paper>
        </div>
    )
}