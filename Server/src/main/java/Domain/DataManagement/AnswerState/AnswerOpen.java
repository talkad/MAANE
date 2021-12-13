package Domain.DataManagement.AnswerState;

import Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;

public class AnswerOpen implements Answer{

    private String answer;
    private final AnswerType type;

    public AnswerOpen(String answer, AnswerType type) {
        this.answer = answer;
        this.type = type;
    }

    @Override
    public Response<Boolean> addAnswer(String answer) {
        return new Response<>(false, true, "wrong answer type");
    }

    @Override
    public Response<Boolean> removeAnswer(int answerID) {
        return new Response<>(false, true, "wrong answer type");
    }

    @Override
    public Response<List<String>> getAnswers() {
        return new Response<>(new LinkedList<>(), true, "wrong answer type");
    }

//    @Override
//    public Response<Boolean> fillAnswer(String answer) {
//        if(type == VERBAL_ANSWER)
//        {
//            this.answer = answer;
//            return new Response<>(true, false, "legal answer");
//        }
//        else{
//
//            try{ // check if integer
//                int num = Integer.parseInt(answer);
//                this.answer = answer;
//
//                return new Response<>(true, false, "legal answer");
//            }catch (NumberFormatException e){
//                return new Response<>(false, true, "illegal number format");
//            }
//        }
//
//    }


}
