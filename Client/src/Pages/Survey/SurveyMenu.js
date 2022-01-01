import React, { useState, useEffect } from "react";
import * as Space from 'react-spaces';
import {Button, Card, CardActionArea, CardActions, CardContent} from "@mui/material";
import Typography from "@mui/material/Typography";
import { useNavigate } from "react-router-dom";

const data =
    [
        {
            id: 1,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 2,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 3,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 4,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 5,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 6,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },
        {
            id: 7,
            title: "hello there",
            description: "general kenobi",
        },

    ]

export default function SurveyMenu(){
    let navigate = useNavigate();

    // getting a list and an amount
    // returning a list of lists where in each sub-list there is at most amountInList elements
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
            <Space.Top size="10%">
                <Space.Right size="5%"/>
                <Space.Fill size>
                    <h1>הסקרים שלי</h1>
                </Space.Fill>
            </Space.Top>
            <Space.Fill scrollable={true}>
                {listOfLists(data, 3).map(x =>
                    <Space.Top size="30%">
                        <Space.Right size="5%"/>
                        {x.map(y =>
                            <Space.Right size="30%">
                                <Card variant="outlined" sx={{minHeight: "90%", maxWidth: "90%"}}>
                                    <CardActionArea>
                                        <CardContent>
                                            <Typography variant="h5" component="div">
                                                {y.title}
                                            </Typography>
                                            <Typography variant="body2">
                                                {y.description}
                                            </Typography>
                                        </CardContent>
                                        <CardActions>
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