import React, { useState } from "react";
import '../SurveyBuilder/SurveryBuilderQuestion.css'
import {FormControlLabel, FormLabel, Grid, Paper, Radio, RadioGroup} from "@mui/material";
import TextField from "@mui/material/TextField";
import FormControl from '@mui/material/FormControl';

// TODO: change to react-space

export default function SurveyQuestion(props){
    const [openAnswer, setOpenAnswer] = useState('');
    const [multipleChoiceAnswer, setMultipleChoiceAnswer] = useState(0);

    const answer_label_string = 'תשובה';
    const numeral_answer_label_string = 'תשובה מספרית'
    const open_answer_helper_text_string = 'נא להזין תשובה מספרית בלבד'

    const handleOpenAnswerChange = (event) => {
        setOpenAnswer(event.target.value)
        props.answerChange(props.id, event.target.value)
    }

    const handleMultipleChoiceAnswerChange = (event) => {
        setMultipleChoiceAnswer(event.target.value);
        props.answerChange(props.id, event.target.value)
    }


    return (
        <div className="SurveyQuestion">
            <Paper className="Survey-paper" elevation={3}>
                <Grid container spacing={2}>
                    <Grid item xs={9}>
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