import * as Space from 'react-spaces';
import {useEffect, useState} from "react";
import Connection from "../../Communication/Connection";
import {Button} from "@mui/material";
import SurveyRule from "./SurveyRule";


export default function SurveyRulesEditor(){

    const [id, setId] = useState('');

    const [rules, setRules] = useState([{id: 1, children: [{id: 4, children: [{id: 5, children: []}]}]}, {id: 2, children: []}])
    const [ruleID, setRuleID] = useState(10);

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
        setRules([...rules, {id: 3}])
        setRuleID(ruleID + 1)
    }

    /**
     * adds a new condition in a given sub-rule
     * @param id the id of the sub-rule to add the new condition to
     * @param trace the trace of parents of the sub-rule
     */
    const addCondition = (id, trace) => {
        console.log(`the trace of id ${id} is ${trace}`)
        let temp_trace = [...trace];
        setRules(rules.map(function(rule) {

            if(rule.id !== id && !temp_trace.includes(rule.id)){ // the id is not part of the trace or the current element
                return rule;
            }

            if(rule.id === id){
                const temp_rule = {...rule};
                temp_rule.children.push({id: ruleID, children: []});
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
                    temp_child.children.push({id: ruleID, children: []});
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

            current_rule.children = current_rule.children.map(find_and_add)

            // while(trace.length !== 0){
            //     const next_trace_id = trace.shift()
            //     current_rule = {...current_rule.children.find(child => child.id === next_trace_id)}
            // }
            //
            // // now we are at its parent rule
            // current_rule = {...current_rule.children.find(child => child.id === id)}
            //
            // // we found it and adding the condition to it
            // current_rule.children.push({id: ruleID, children: []})
            // setRuleID(ruleID + 1)


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
                    <SurveyRule id={rule.id} children={rule.children} trace={[]} addCondition={addCondition}/>)}

                {/*add rule button*/}
                <Button onClick={() => addRule()} variant={'contained'}>{add_rules_button_string}</Button>
            </div>
        </Space.Fill>
    )
}