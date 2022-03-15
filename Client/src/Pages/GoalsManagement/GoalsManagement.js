import React, { useState, useEffect } from "react";
import './GoalsManagement.css'
import * as Space from 'react-spaces';
import {
    Box,
    Collapse,
    IconButton, List, ListItem, ListItemText,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import Connection from "../../Communication/Connection";

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

const rows = [
    createData(0, "the tragedy of darth plaguis the wise", "Did you ever hear the tragedy of Darth Plagueis The Wise? I thought not. It’s not a story the Jedi would tell you. It’s a Sith legend. Darth Plagueis was a Dark Lord of the Sith, so powerful and so wise he could use the Force to influence the midichlorians to create life… He had such a knowledge of the dark side that he could even keep the ones he cared about from dying. The dark side of the Force is a pathway to many abilities some consider to be unnatural. He became so powerful… the only thing he was afraid of was losing his power, which eventually, of course, he did. Unfortunately, he taught his apprentice everything he knew, then his apprentice killed him in his sleep. Ironic. He could save others from death, but not himself.", 1, 3),
]

export default function GoalsManagement(props){
    const [tableRows, setTableRows] = useState(rows);

    const page_title_string = "ניהול יעדים";
    const goal_title_cell_head_string = "יעד";
    const goal_quarter_cell_head_string = "רבעון";
    const goal_weight_cell_head_string = "משקל";

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
                <TableContainer sx={{width: "80%"}} component={Paper}>
                    <Table aria-label="collapsible table">
                        <TableHead>
                            <TableRow>
                                <TableCell/>
                                <TableCell>{goal_title_cell_head_string}</TableCell>
                                <TableCell>{goal_quarter_cell_head_string}</TableCell>
                                <TableCell>{goal_weight_cell_head_string}</TableCell>
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