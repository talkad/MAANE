import React, {useEffect, useState} from "react";
import * as Space from 'react-spaces';
import {
    Button,
    Card,
    CardActionArea,
    CardActions,
    CardContent,
    Dialog,
    DialogContent, DialogContentText,
    DialogTitle,
    Grid
} from "@mui/material";
import Typography from "@mui/material/Typography";
import { useNavigate } from "react-router-dom";
import Connection from "../../Communication/Connection";
import DialogContext from "@mui/material/Dialog/DialogContext";

// todo: option to delete a survey


/**
 * a dialog element for publishing a survey
 * @param props the properties the element gets
 * @returns {JSX.Element} the element
 */
function PublishSurveyDialog(props){

    const title_string = "האם את/ה בטוח/ה שהינך רוצה לשלוח את הסקר";
    const disclaimer_string = "לא יהיה ניתן לערוך את הסקר לאחר השליחה";
    const send_string = "שליחה";
    const cancel_string = "ביטול";

    const handleSubmitDeletion = () => {
        props.callback(props.selectedSurveyID);
    }

    return (
        <Dialog titleStyle={{textAlign: "center"}} sx={{alignItems: "right"}} fullWidth maxWidth="sm" onClose={props.onClose} open={props.open}>
            <DialogTitle>
                <Typography variant="h5" align="center">{title_string}</Typography>
                <Typography color={'blue'} variant="h5" align="center">?{props.selectedSurveyTitle}</Typography>
            </DialogTitle>
            <DialogContent>
                <DialogContentText>
                    <Typography variant={'h6'} align="center">{disclaimer_string}</Typography>
                </DialogContentText>
            </DialogContent>
            <Grid container justifyContent="center" spacing={0}>
                <Grid item align="center" xs={6}>
                    {/*the cancel button*/}
                    <Button id={"send_survey_submit_button"} onClick={() => props.onClose()} sx={{marginBottom: 1, width: "50%"}} color={'error'} variant="outlined">{cancel_string}</Button>
                </Grid>
                <Grid item align="center" xs={6}>
                    {/*the delete button*/}
                    <Button id={"send_survey_cancel_button"} onClick={() => handleSubmitDeletion()} sx={{marginBottom: 1, width: "50%"}} color="success" variant="outlined">{send_string}</Button>
                </Grid>
            </Grid>
        </Dialog>
    )
}


//data for testing offline
const offline_data =
    [
        {
            id: 1,
            title: "סקר 2018",
            description: "רוית",
        },
        {
            id: 2,
            title: "סקר 2019",
            description: "רוית",
        },
        {
            id: 3,
            title: "סקר 2020",
            description: "רוית",
        },
        {
            id: 4,
            title: "סקר 2021",
            description: "רוית",
        },
        {
            id: 6,
            title: "hello there",
            description: "general kenobi",
        },

    ]

export default function SurveyMenu(props){
    const [data, setData] = useState(offline_data);

    // publish survey data
    const [openPublishSurveyDialog, setOpenPublishSurveyDialog] = useState(false);
    const [selectedSurveyTitle, setSelectedSurveyTitle] = useState('');
    const [selectedSurveyID, setSelectedSurveyID] = useState('');

    let navigate = useNavigate();

    const create_survey_button_string = "יצירת סקר";

    useEffect(() => {
        props.setHideBars(false);

        new Connection().getCreatedSurveys(arrangeSurveys);
    }, []);

    /**
     * arranges the survey data received from the server
     * @param data the data from the server
     */
    const arrangeSurveys = (data) => {
        console.log(data);
        if (!data.failure){
            data.result.forEach(survey => setData(data => [...data, {id: survey.id, title: survey.title, description: survey.description}]));
        }
    }

    // getting a list and an amount
    // returning a list of lists where in each sub-list there is at most amountInList elements
    /**
     * returns a list of lists where in each sub-list there is at most amountInList elements
     * @param list list of elements
     * @param amountInList amout for each sublist
     * @returns {*[]} list of sub-lists of the param list
     */
    const listOfLists = (list, amountInList) => {
        let toReturn = [];
        let index = 0;

        while (index * amountInList < list.length){
            toReturn.push(list.slice(amountInList * index, amountInList * (index+1)));
            index++;
        }

        toReturn.push(list.slice(amountInList * index));

        return toReturn;
    }

    // PUBLISH SURVEY FUNCTIONS

    const handleOpenPublishSurveyDialog = (surveyID, surveyTitle) => {
        setOpenPublishSurveyDialog(true);
        setSelectedSurveyTitle(surveyTitle);
        setSelectedSurveyID(surveyID);
    }

    /**
     * on close handler for the publish survey dialog
     */
    const handleClosePublishSurveyDialog = () => {
        setOpenPublishSurveyDialog(false);
    }

    /**
     * handler for sending
     * @param surveyID the id of the survey to publish
     */
    const handlePublishSurvey = (surveyID) => {
        setOpenPublishSurveyDialog(false);

        props.setAuthCallBack(() => () => alert(surveyID));
        props.setAuthAvailability(true);
        props.setAuthCalleePage('../../survey/menu');
        props.setAuthGoToPage('../../survey/menu');
        navigate(`../../user/auth`, {replace: true})
    }


    return (
        <Space.Fill>
            <Space.Top size="20%">
                <Space.Right size="5%"/>
                {/*title of the menu*/}
                <Space.Fill size>
                    <h1>הסקרים שלי</h1>
                    <Button variant={"contained"} onClick={() => navigate(`../createSurvey`, {replace: false})} >{create_survey_button_string}</Button>
                </Space.Fill>
            </Space.Top>
            {/*container for all the elements of the menu*/}
            <Space.Fill scrollable={true}>
                {listOfLists(data, 3).map(x =>
                    <Space.Top size="30%">
                        <Space.Right size="5%"/>
                        {x.map(y =>
                            <Space.Right size="30%">
                                {/*container for each element in the menu*/}
                                <Card variant="outlined" sx={{minHeight: "90%", maxWidth: "90%"}}>
                                    <CardActionArea>
                                        <CardContent>
                                            {/*description of the survey*/}
                                            <Typography variant="h5" component="div">
                                                {y.title}
                                            </Typography>
                                            <Typography variant="body2">
                                                {y.description}
                                            </Typography>
                                        </CardContent>
                                        <CardActions>
                                            {/*button to go to the survey represented by the card this button is in*/}
                                            <Button color={'secondary'} size={"medium"} onClick={() => navigate(`../createSurvey?surveyID=${y.id}`, {replace: true})}>עריכת סקר</Button>
                                            <Button color="secondary" size="medium" onClick={() => navigate(`../getSurvey?surveyID=${y.id}`, {replace: true})}>מעבר לסקר</Button>
                                            <Button color={'secondary'} size={'medium'} onClick={() => navigate(`../rules?surveyID=${y.id}`, {replace: false})}>חוקים</Button>                                            <Button color={'secondary'} size={'medium'} onClick={() => navigate(`../rules?surveyID=${y.id}`, {replace: false})}>חוקים</Button>
                                            <Button color={'secondary'} size={'medium'} onClick={() => handleOpenPublishSurveyDialog(y.id, y.title)}>publish test</Button>
                                        </CardActions>
                                    </CardActionArea>
                                </Card>
                            </Space.Right>
                        )}
                        <Space.Left size="5%"/>
                    </Space.Top>
                )}
            </Space.Fill>
            <PublishSurveyDialog
                selectedSurveyTitle={selectedSurveyTitle}
                selectedSurveyID={selectedSurveyID}
                open={openPublishSurveyDialog}
                onClose={handleClosePublishSurveyDialog}
                callback={handlePublishSurvey}
            />
        </Space.Fill>
    )
}