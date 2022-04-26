import * as Space from 'react-spaces';
import {useEffect, useState} from "react";
import Connection from "../../Communication/Connection";
import {Button} from "@mui/material";
import SurveyRule from "./SurveyRule";

const offline_data_rules = [{id: 1, goalSelection: 1, children: [{id: 4, questionSelection: '',
        children: [{id: 5, questionSelection: '', children: []}]}]},
    {id: 2, goalSelection: 2, children: []}];
const offline_goals_data = [{value: 1, description: "hello there"}, {value: 2, description: "general kenobi"}];
const offline_questions_data = [{id: 0, question: "sup", type: "MULTIPLE_CHOICE", answer: ['dunno', 'have no idea', 'idk']},
    {id: 1, question: 'up dawg', type: 'NUMERIC_ANSWER', answer: ''},
    {id: 2, question: 'how are you', type: 'MULTIPLE_CHOICE', answer: ['good', 'fine', 'bad']}]
// NUMERIC_ANSWER, MULTIPLE_CHOICE

const color_stack = ['#ffffff', '#d89af5', '#93b6fa']; // todo: add more colors

export default function SurveyRulesEditor(){

    const [id, setId] = useState('');

    const [rules, setRules] = useState(offline_data_rules)
    const [ruleID, setRuleID] = useState(10);

    const [goals, setGoals] = useState(offline_goals_data);
    const [questions, setQuestions] = useState(offline_questions_data);

    const add_rules_button_string = "הוספת חוק";

    useEffect(() => {
        var url = new URL(window.location.href);
        var surveyID = url.searchParams.get("surveyID");
        setId(surveyID)

        // TODO: send for the data

    }, []);

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
    const addCondition = (id, trace) => {
        //console.log(`the trace of id ${id} is ${trace}`)
        let temp_trace = [...trace];
        setRules(rules.map(function(rule) {

            if(rule.id !== id && !temp_trace.includes(rule.id)){ // the id is not part of the trace or the current element
                return rule;
            }

            if(rule.id === id){
                const temp_rule = {...rule};
                temp_rule.children.push({id: ruleID, questionSelection: '', children: []});
                setRuleID(ruleID + 1);

                return temp_rule
            }


            // what's left is the condition where the current rule is in the trace
            // finding the rule to add the condition to
            temp_trace.shift();
            let current_rule = {...rule};

            const find_and_add = function(child) {

                if(temp_trace.length  === 0 && child.id === id){

                    const temp_child = {...child};
                    temp_child.children.push({id: ruleID, questionSelection: '', children: []});
                    setRuleID(ruleID + 1);

                    return temp_child;

                }

                if(temp_trace.length === 0 && child.id !== id){
                    return child;
                }

                if(!temp_trace.includes(child.id)){
                    return child;
                }

                // else the current one is included in the list
                const temp_child = {...child};
                temp_trace.shift();
                temp_child.children = child.children.map(find_and_add)

                return temp_child;
            }

            current_rule.children = current_rule.children.map(find_and_add);

            return current_rule;
        }))
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
        setRules(rules.map(function(rule) {

            if(rule.id !== id && !temp_trace.includes(rule.id)){ // the id is not part of the trace or the current element
                return rule;
            }


            // what's left is the condition where the current rule is in the trace
            // finding the rule to add the condition to
            temp_trace.shift();
            let current_rule = {...rule};

            const find_and_edit = function(child) {

                if(temp_trace.length  === 0 && child.id === id){

                    const temp_child = {...child};
                    temp_child.questionSelection = value;

                    return temp_child;

                }

                if(temp_trace.length === 0 && child.id !== id){
                    return child;
                }

                if(!temp_trace.includes(child.id)){
                    return child;
                }

                // else the current one is included in the list
                const temp_child = {...child};
                temp_trace.shift();
                temp_child.children = child.children.map(find_and_edit)

                return temp_child;
            }

            current_rule.children = current_rule.children.map(find_and_edit);

            return current_rule;
        }))
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
                                addCondition={addCondition} goalSelectionChange={handleGoalSelectionChange}
                                questionSelectionChange={handleQuestionSelectionChange}/>)}

                {/*add rule button*/}
                <Button onClick={() => addRule()} variant={'contained'}>{add_rules_button_string}</Button>
            </div>
        </Space.Fill>
    )
}