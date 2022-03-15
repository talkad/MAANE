import React, { useState, useEffect } from "react";
import './GoalsManagement.css'
import * as Space from 'react-spaces';
import {
    Accordion, AccordionDetails, AccordionSummary,
    Box, Button,
    Collapse, FormControl, Grow,
    IconButton, InputLabel, List, ListItem, ListItemText, MenuItem,
    Paper, Select, Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow, TextField, Typography
} from "@mui/material";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import Connection from "../../Communication/Connection";
import {AccordionButton} from "react-bootstrap";

/**
 * a function that returns an object containing the data for a row in the table
 * @param title the title of the goal
 * @param description the description of the goal
 * @param weight the weight of the goal
 * @param quarter the quarter of the goal
 * @returns {{description, weight, title, quarter}}
 */
function createData(id, title, description, quarter, weight) {
    return {
        id,
        title,
        description,
        quarter,
        weight
    }
}


/**
 * this function is a hook of a row in the table
 * @param props data for a row
 * @returns {JSX.Element} JSX element of the row
 */
function Row(props) {
    const { row } = props;
    const [open, setOpen] = React.useState(false);

    const goal_description_string = "תיאור היעד";

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
                {/* the arrow to open the extra info */}
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>
                {/* main goal's info */}
                <TableCell>{row.title}</TableCell>
                <TableCell>{row.quarter}</TableCell>
                <TableCell>{row.weight}</TableCell>
            </TableRow>
            {/*secondary user's into*/}
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0}} colSpan={6}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1}}>
                            <List
                                sx={{
                                    width: "40%",
                                    bgcolor: "background.paper",
                                }}
                            >
                                <ListItem>
                                    <ListItemText primary={goal_description_string} secondary={row.description}/>
                                </ListItem>
                            </List>
                        </Box>

                    </Collapse>
                </TableCell>
            </TableRow>

        </React.Fragment>
    )

}

function NewGoalForm(props) {
    const [weight, setWeight] = useState(1);
    const [quarter, setQuarter] = useState(1);

    const form_title_string = "הוספת יעד חדש";
    const form_title_field_label_string = "כותרת היעד";
    const form_description_field_label_string = "תיאור היעד";
    const form_quarter_string = "רבעון השלמת היעד";
    const form_weight_string = "משקל היעד";
    const form_add_button_string = "הוספ/י יעד";

    /**
     * handles the change of the selection of quarter
     * @param event the selection element bound to the function
     */
    const handleQuarterChange = (event) => {
        setQuarter(event.target.value);
    };

    /**
     * handles the change of the selection of weight
     * @param event the selection element bound to the function
     */
    const handleWeightChange = (event) => {
        setWeight(event.target.value);
    };

    /**
     * handles the submission of the add goal form (collecting and sending the data of the form)
     * @param event
     */
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);

        // todo: collect data and send
    }

    return (
            <Paper sx={{marginTop: "1%"}} elevation={2}>
                <Typography variant="h5">{form_title_string}</Typography>
                <Stack
                    component="form"
                    onSubmit={handleSubmit}
                    spacing={2}
                    sx={{
                        '& .MuiTextField-root': { m: 1, width: '50%' },
                    }}
                    noValidate
                    autoComplete="off">

                    {/*form title*/}
                    <TextField
                        name="title"
                        label={form_title_field_label_string}
                        width
                    />

                    {/*form description*/}
                    <TextField
                        name="description"
                        label={form_description_field_label_string}
                        multiline
                        rows={4}
                    />

                    {/*form quarter select*/}
                    <FormControl sx={{width: "20%"}}>
                        <InputLabel id="quarter-label">{form_quarter_string}</InputLabel>
                        <Select
                            labelId="quarter-label"
                            value={quarter}
                            label={form_quarter_string}
                            onChange={handleQuarterChange}
                        >
                            <MenuItem value={1}>1</MenuItem>
                            <MenuItem value={2}>2</MenuItem>
                            <MenuItem value={3}>3</MenuItem>
                            <MenuItem value={4}>4</MenuItem>
                        </Select>
                    </FormControl>

                    {/*form weight select*/}
                    <FormControl sx={{width: "20%"}}>
                        <InputLabel id="weight-label">{form_weight_string}</InputLabel>
                        <Select
                            labelId="weight-label"
                            value={weight}
                            label={form_weight_string}
                            onChange={handleWeightChange}
                        >
                            <MenuItem value={1}>1</MenuItem>
                            <MenuItem value={2}>2</MenuItem>
                            <MenuItem value={3}>3</MenuItem>
                            <MenuItem value={4}>4</MenuItem>
                            <MenuItem value={5}>5</MenuItem>
                            <MenuItem value={6}>6</MenuItem>
                            <MenuItem value={7}>7</MenuItem>
                            <MenuItem value={8}>8</MenuItem>
                            <MenuItem value={9}>9</MenuItem>
                            <MenuItem value={10}>10</MenuItem>
                        </Select>
                    </FormControl>

                    {/*TODO: add hebrew year selection*/}

                    {/*form submit button*/}
                    <Button variant={"outlined"} sx={{width: "20%", marginBottom: "1%"}}>{form_add_button_string}</Button>
                </Stack>
            </Paper>
    );
}

const rows = [
    createData(0, "the tragedy of darth plaguis the wise", "Did you ever hear the tragedy of Darth Plagueis The Wise? I thought not. It’s not a story the Jedi would tell you. It’s a Sith legend. Darth Plagueis was a Dark Lord of the Sith, so powerful and so wise he could use the Force to influence the midichlorians to create life… He had such a knowledge of the dark side that he could even keep the ones he cared about from dying. The dark side of the Force is a pathway to many abilities some consider to be unnatural. He became so powerful… the only thing he was afraid of was losing his power, which eventually, of course, he did. Unfortunately, he taught his apprentice everything he knew, then his apprentice killed him in his sleep. Ironic. He could save others from death, but not himself.", 1, 3),
]

export default function GoalsManagement(props){
    const [tableRows, setTableRows] = useState(rows);
    const [showNewGoalForm, setShowNewGoalForm] = useState(false);

    const page_title_string = "ניהול יעדים";
    const goal_title_cell_head_string = "יעד";
    const goal_quarter_cell_head_string = "רבעון";
    const goal_weight_cell_head_string = "משקל";
    const add_goal_button_string = "הוספ/י יעד";

    useEffect(() => {
        //TODO: send request to server
    }, []);

    /**
     * handler for the response from the server for the table data
     * @param data the table data to arrange
     */
    const handleReceivedData = (data) => {
        //todo: implement
    }

    return (
        <Space.Fill scrollable>
            <div id="Manage-goals">
                <h1>{page_title_string}</h1>
                {/*todo: make the add button stick to the right*/}
                <Button onClick={() => setShowNewGoalForm(!showNewGoalForm)} variant="outlined">{add_goal_button_string}</Button>
                <Collapse sx={{width: "50%"}} in={showNewGoalForm}><NewGoalForm/></Collapse>
                <TableContainer sx={{width: "80%", marginTop: "1%"}} component={Paper}>
                    <Table aria-label="collapsible table">
                        <TableHead>
                            <TableRow>
                                <TableCell/>
                                <TableCell>{goal_title_cell_head_string}</TableCell>
                                <TableCell>{goal_quarter_cell_head_string}</TableCell>
                                <TableCell>{goal_weight_cell_head_string}</TableCell>
                            {/*    TODO: add delete goal*/}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {tableRows.map((tableRow) => (
                                <Row key={tableRow.id} row={tableRow}/>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </div>
        </Space.Fill>
    );
}