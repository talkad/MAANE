import React, { useState, useEffect } from "react";
import * as Space from 'react-spaces';

export default function PasswordAuthentication(props){

    useEffect(() => {
        console.log("hello there");
        props.setHideBars(true);
    });

    return (
        <Space.Centered>
            <h1>hello there</h1>
        </Space.Centered>
    )
}