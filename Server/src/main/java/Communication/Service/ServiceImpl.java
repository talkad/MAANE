package Communication.Service;

import Communication.Model.Instructor;
import Communication.Repo.Repo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

//@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServiceImpl implements ServerService{

    private final Repo repo = new Repo();

    @Override
    public void add(Instructor ins) {
        log.info("add new instructor");

        repo.add(ins);
    }

    @Override
    public void delete(int index) {
        log.info("delete instructor");

        repo.delete(index);
    }

    @Override
    public Instructor getIns(int index) {
        log.info("get instructor");

        return repo.getIns(index);
    }

    @Override
    public Collection<Instructor> list() {
        log.info("get all");

        return repo.getList();
    }
}
