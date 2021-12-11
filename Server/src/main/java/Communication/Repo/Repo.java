package Communication.Repo;

import Communication.Model.Instructor;

import java.util.LinkedList;
import java.util.List;

public class Repo {
    private List<Instructor> list;

    public Repo(){
        list = new LinkedList<>();
    }

    public void add(Instructor ins){
        list.add(ins);
    }

    public void delete(int index){
        list.remove(index);
    }

    public Instructor getIns(int index){
        return list.get(index);
    }

    public List<Instructor> getList() {
        return list;
    }
}
