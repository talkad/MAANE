import React, { useState } from "react";
import './SurveryQuestion.css'
import {Grid, Paper} from "@mui/material";
import TextField from "@mui/material/TextField";

export default function Survey(props) {
    const [id, setID] = useState(props.id);

    const question_label_string = 'שאלה'

    return (
        <div className="SurveyQuestion">
            <Paper className="Survey-paper" elevation={3}>
                <Grid container spacing={2}>
                    <Grid item xs={9}>
                        <TextField
                            className="SurveyQuestion-text-field"
                            margin="normal"
                            variant="filled"
                            required
                            id="title"
                            label={question_label_string}
                            name="title"
                            autoFocus
                        />
                    </Grid>
                    <Grid item xs={3}>
                        hello there
                    </Grid>
                </Grid>

            </Paper>
        </div>
    )

}