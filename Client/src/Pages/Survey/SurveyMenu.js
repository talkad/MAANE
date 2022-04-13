import React from "react";
import * as Space from 'react-spaces';
import {Button, Card, CardActionArea, CardActions, CardContent} from "@mui/material";
import Typography from "@mui/material/Typography";
import { useNavigate } from "react-router-dom";

// todo: option to delete a survey
// todo: option to send the survey to the coordinators

// data for testing offline
const data =
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
        }
        // {
        //     id: 6,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },
        // {
        //     id: 7,
        //     title: "hello there",
        //     description: "general kenobi",
        // },

    ]

export default function SurveyMenu(){
    let navigate = useNavigate();

    const create_survey_button_string = "יצירת סקר";

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
                                            <Button color="secondary" size="medium" onClick={() => navigate(`../getSurvey?id=${y.id}`, {replace: true})}>מעבר לסקר</Button>
                                        </CardActions>
                                    </CardActionArea>
                                </Card>
                            </Space.Right>
                        )}
                        <Space.Left size="5%"/>
                    </Space.Top>
                )}
            </Space.Fill>
        </Space.Fill>
    )
}