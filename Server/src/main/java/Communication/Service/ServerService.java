package Communication.Service;

import Communication.Model.Instructor;

import java.util.Collection;

public interface ServerService {
    void add(Instructor ins);

    void delete(int index);

    Instructor getIns(int index);

    Collection<Instructor> list();

}
