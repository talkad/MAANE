import * as Space from 'react-spaces';
import {useEffect, useState} from "react";
import Connection from "../../Communication/Connection";
import {Button} from "@mui/material";
import SurveyRule from "./SurveyRule";

const offline_data_rules = [{id: 1, goalSelection: 1, children: [{id: 4, questionSelection: '',
        children: [{id: 5, questionSelection: '', children: [], constraint: {type: '', value: ''}}], constraint: {type: '', value: ''}}]},
    {id: 2, goalSelection: 2, children: []}];
const offline_goals_data = [{value: 1, description: "hello there"}, {value: 2, description: "general kenobi"}];
const offline_questions_data = [{id: 0, question: "sup", type: "MULTIPLE_CHOICE", answers: ['dunno', 'have no idea', 'idk']},
    {id: 1, question: 'up dawg', type: 'NUMERIC_ANSWER', answers: ''},
    {id: 2, question: 'how are you', type: 'MULTIPLE_CHOICE', answers: ['good', 'fine', 'bad']}]
// NUMERIC_ANSWER, MULTIPLE_CHOICE

//const color_stack = ['#ffffff', '#d89af5', '#93b6fa']; // todo: add more colors

const color_stack = ['#ffffff', '#E6B0AA', '#D7BDE2', '#A9CCE3', '#A3E4D7', '#A9DFBF', '#F9E79F', '#F5CBA7']; // todo: add more colors

export default function SurveyRulesEditor(){

    const [id, setId] = useState('');

    const [rules, setRules] = useState(offline_data_rules)
    const [ruleID, setRuleID] = useState(10);

    const [goals, setGoals] = useState(offline_goals_data);
    const [questions, setQuestions] = useState(offline_questions_data);

    const add_rules_button_string = "הוספת חוק";
    const submit_rules_button_string = "סיום ושמירה";

    useEffect(() => {
        var url = new URL(window.location.href);
        var surveyID = url.searchParams.get("surveyID");
        setId(surveyID)

        // TODO: send for the data

    }, []);

    /**
     * arranges the data received from the server regarding the goals
     * @param data the data from the server
     */
    const arrangeGoalsData = (data) => {

    }

    /**
     * arranged the data received from the server regarding the question
     * @param data the data from the server
     */
    const arrangeQuestionData = (data) => {

    }

    /**
     * adds a new rule
     */
    const addRule = () => {
        setRules([...rules, {id: ruleID, goalSelection: 1, children: []}])
        setRuleID(ruleID + 1)
    }

    /**
     * adds a new condition in a given sub-rule
     * @param id the id of the sub-rule to add the new condition to
     * @param trace the trace of parents of the sub-rule
     */
    const addCell = (id, trace) => {
        //console.log(`the trace of id ${id} is ${trace}`)
        let temp_trace = [...trace];

        const find_and_add = function(cell) {

            if(temp_trace.length  === 0 && cell.id === id){

                const temp_cell = {...cell};
                temp_cell.children.push({id: ruleID, questionSelection: '', children: [], constraint: {type: '', value: ''}});
                setRuleID(ruleID + 1);

                return temp_cell;

            }

            if(temp_trace.length === 0 && cell.id !== id){
                return cell;
            }

            if(!temp_trace.includes(cell.id)){
                return cell;
            }

            // else the current one is included in the list
            const temp_cell = {...cell};
            temp_trace.shift();
            temp_cell.children = cell.children.map(find_and_add)

            return temp_cell;
        }

        setRules(rules.map(find_and_add))
    }

    /**
     * removed a given cell
     * @param id the id of the cell to remove
     * @param trace the trace of the cell to remove
     */
    const removeCell = (id, trace) => {
        // TODO: add a warning that it's going to delete everything in that cell
        //console.log(`the trace of id ${id} is ${trace}`)
        let temp_trace = [...trace];

        const find_and_remove = function(cell) {

            if(!temp_trace.includes(cell.id)){
                return cell;
            }

            // else the current one is included in the list
            const temp_child = {...cell};
            temp_trace.shift();

            if(temp_trace.length === 0){
                temp_child.children = cell.children.filter((sub_cell) => sub_cell.id !== id)
            }
            else{
                temp_child.children = cell.children.map(find_and_remove)
            }


            return temp_child;
        }

        if(temp_trace.length === 0){
            setRules(rules.filter((cell) => cell.id !== id))
        }
        else{
            setRules(rules.map(find_and_remove))
        }
    }

    /**
     * handler for the onChange of a goal select element
     * @param id the id of the cell affected
     * @param value the new value which has been selected
     */
    const handleGoalSelectionChange = (id, value) => {
        setRules(rules.map(rule => {
            if(rule.id !== id){
                return rule;
            }

            const temp_rule = {...rule};
            temp_rule.goalSelection = value;

            return temp_rule;
        }))
    }

    /**
     * handler for the onChange of a question select element
     * @param id the id of the cell in which the change occurred
     * @param trace the trace of parents to the child
     * @param value the value to change to
     */
    const handleQuestionSelectionChange = (id, trace, value) => {
        let temp_trace = [...trace];

        const getQuestionType = (id) => {
            if(id === undefined){
                return '';
            }

            if(id === '' || id === 'AND' || id === 'OR'){
                return '';
            }

            const question = questions.find(element => element.id === id);
            return question.type;
        }

        const find_and_edit = function(cell) {

            if(temp_trace.length  === 0 && cell.id === id){

                const temp_cell = {...cell};

                // TODO: currently we are removing all the children if going from AND or OR to a question.
                // TOOD: should we do it? and if so, should we raise an error?
                if((value !== "AND" && value !== "OR") &&
                    (temp_cell.questionSelection === "AND" || temp_cell.questionSelection === "OR" || temp_cell.questionSelection === "")){

                    if(getQuestionType(value) === "MULTIPLE_CHOICE"){
                        temp_cell.constraint.type = "MULTIPLE_CHOICE";
                        temp_cell.constraint.value = [];
                    }


                    temp_cell.children = [];
                }

                if((value === "AND" || value === "OR") &&
                    (temp_cell.questionSelection !== "AND" && temp_cell.questionSelection !== "OR")){

                    if (temp_cell.children.length === 0){
                        temp_cell.children = [{id: ruleID, questionSelection: '', children: [], constraint: {type: '', value: ''}}];
                        setRuleID(ruleID + 1);
                    }
                }

                temp_cell.questionSelection = value;



                return temp_cell;

            }

            if(temp_trace.length === 0 && cell.id !== id){
                return cell;
            }

            if(!temp_trace.includes(cell.id)){
                return cell;
            }

            // else the current one is included in the list
            const temp_cell = {...cell};
            temp_trace.shift();
            temp_cell.children = cell.children.map(find_and_edit)

            return temp_cell;
        }

        setRules(rules.map(find_and_edit))
    }

    /**
     * handler for the change in the checkboxes of a cell
     * @param id the id of the cell
     * @param trace the trace of the cell
     * @param value the checkbox which got checked
     */
    const handleConstraintValueChoiceChange = (id, trace, value) => {
        let temp_trace = [...trace];

        const find_and_edit = function(cell) {

            if(temp_trace.length  === 0 && cell.id === id){

                const temp_cell = {...cell};

                if(temp_cell.constraint.value.includes(value)){
                    temp_cell.constraint.value = temp_cell.constraint.value.filter(ele => ele !== value)
                }
                else{

                    temp_cell.constraint.value.push(value);
                }

                return temp_cell;

            }

            if(temp_trace.length === 0 && cell.id !== id){
                return cell;
            }

            if(!temp_trace.includes(cell.id)){
                return cell;
            }

            // else the current one is included in the list
            const temp_cell = {...cell};
            temp_trace.shift();
            temp_cell.children = cell.children.map(find_and_edit)

            return temp_cell;
        }

        setRules(rules.map(find_and_edit))
    }

    /**
     * handler for the change of a numerical question constraint
     * @param id the id of the cell the question is in
     * @param trace the trace of the cell the question is in
     * @param type the new type of constraint for the question (new inequality)
     * @param value the new constraint value (numerical)
     */
    const handleNumericalConstraintChange = (id, trace, type, value) => {
        let temp_trace = [...trace];

        const find_and_edit = function(cell) {

            if(temp_trace.length  === 0 && cell.id === id){

                const temp_cell = {...cell};

                temp_cell.constraint.type = type;
                temp_cell.constraint.value = value;

                return temp_cell;

            }

            if(temp_trace.length === 0 && cell.id !== id){
                return cell;
            }

            if(!temp_trace.includes(cell.id)){
                return cell;
            }

            // else the current one is included in the list
            const temp_cell = {...cell};
            temp_trace.shift();
            temp_cell.children = cell.children.map(find_and_edit)

            return temp_cell;
        }

        setRules(rules.map(find_and_edit))
    }

    const submitRules = () => {
        // TODO: checking everything is correct

        // TODO: send
    }

    return (
        <Space.Fill scrollable>
            <div className="Survey">
                {/*title*/}
                <h1>rules {id}</h1>

                {/*the rules*/}
                {rules.map((rule) =>
                    <SurveyRule id={rule.id} depth={0} colors={color_stack} goalSelection={rule.goalSelection}
                                children={rule.children} trace={[]} goals={goals} questions={questions}
                                addCell={addCell} removeCell={removeCell} goalSelectionChange={handleGoalSelectionChange}
                                handleConstraintValueChoiceChange={handleConstraintValueChoiceChange}
                                questionSelectionChange={handleQuestionSelectionChange}
                                handleNumericalConstraintChange={handleNumericalConstraintChange}/>)}

                {/*add rule button*/}
                <Button onClick={() => addRule()} variant={'contained'}>{add_rules_button_string}</Button>
                <Button onClick={() => submitRules()} variant={'contained'} color={'success'} sx={{marginTop: 1}}>{submit_rules_button_string}</Button>
            </div>
        </Space.Fill>
    )
}