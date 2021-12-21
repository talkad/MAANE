import React, { useState } from "react";
import '../SurveyBuilder/SurveyBuilder.css'
import {Pagination, Paper, TextField} from "@mui/material";
import Button from "@mui/material/Button";
import SurveyQuestion from "./SurveyQuestion";

const questionsPerPage = 5



export default function Survey(){
    // TODO: remove this once we get the info from the server
    const handleAnswerChange = (questionID, value) => {
        const index = questions.findIndex(element => element['id'] === questionID);

        questions[index]['answer'] = value;
    }

    const [surveyTitle, setSurveyTitle] = useState('Hello there');
    const [surveyDescription, setSurveyDescription] = useState('General Kenobi');
    const [questionID, setQuestionID] = useState(0);
    const [questions, setQuestions] = useState([
        {
            id: 0,
            element: <SurveyQuestion id={0} questionString='שלום שם' type='open' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open',
            answer: '',
        },
        {
            id: 1,
            element: <SurveyQuestion id={1} questionString='גנרל קנובי' type='open-number' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open-number',
            answer: '',
        },
        {
            id: 2,
            element: <SurveyQuestion id={2} questionString='אתה עז מצח' type='multiple' answerChange={handleAnswerChange}
                                     answers={['אנקין', 'פדמה',]} />,
            question: '',
            type: 'multiple',
            answer: '',
        },
        {
            id: 1,
            element: <SurveyQuestion id={1} questionString='גנרל קנובי' type='open-number' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open-number',
            answer: '',
        },
        {
            id: 1,
            element: <SurveyQuestion id={1} questionString='גנרל קנובי' type='open-number' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open-number',
            answer: '',
        },
        {
            id: 1,
            element: <SurveyQuestion id={1} questionString='גנרל קנובי' type='open-number' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open-number',
            answer: '',
        },
        {
            id: 1,
            element: <SurveyQuestion id={1} questionString='גנרל קנובי' type='open-number' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open-number',
            answer: '',
        },
        {
            id: 1,
            element: <SurveyQuestion id={1} questionString='גנרל קנובי' type='open-number' answerChange={handleAnswerChange} />,
            question: '',
            type: 'open-number',
            answer: '',
        },
    ]);
    const [page, setPage] = React.useState(1);

    const submit_survey_string = 'סיום מענה';

    // TODO: return this once we get the info from the server
    // const handleAnswerChange = (questionID, value) => {
    //     const index = questions.findIndex(element => element['id'] === questionID);
    //
    //     questions[index]['answer'] = value;
    // }

    const handlePageChange = (event, value) => {
        setPage(value);
    };

    // TODO: have a function which gets the data from the server and sets the states

    const handleSubmit = () => {
        // TODO: send the survey
    }

    return (
        <div style={{margin: '5vh'}} className="Survey">
            <Paper className="Survey-paper" elevation={3}>
                {/*TODO: have this big and in bold*/}
                <TextField
                    color="secondary"
                    margin="normal"
                    className="Survey-text-field"
                    defaultValue={surveyTitle}
                    InputProps={{
                        readOnly: true,
                    }}
                    variant="standard"
                />
                {/*TODO: have this a little smaller*/}
                <TextField
                    color="secondary"
                    margin="normal"
                    className="Survey-text-field"
                    defaultValue={surveyDescription}
                    InputProps={{
                        readOnly: true,
                    }}
                    variant="standard"
                />
            </Paper>

            {questions.slice((page-1) * questionsPerPage, Math.min(page * questionsPerPage, questions.length)).map(x => x['element'])}

            <Pagination count={Math.ceil(questions.length/questionsPerPage)} page={page} onChange={handlePageChange} />
            <br/>
            {page === Math.ceil(questions.length/questionsPerPage) && <Button color="secondary" variant="contained" onClick={handleSubmit}>{submit_survey_string}</Button>}

        </div>
    )
}