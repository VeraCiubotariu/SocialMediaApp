package ir.map.gr222.sem7.service;

import ir.map.gr222.sem7.domain.Message;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.repository.MessageDBRepository;
import ir.map.gr222.sem7.repository.PagingRepository.MessageDBPagingRepository;
import ir.map.gr222.sem7.repository.PagingRepository.Page;
import ir.map.gr222.sem7.repository.PagingRepository.Pageable;
import ir.map.gr222.sem7.repository.UserDBRepository;
import ir.map.gr222.sem7.utils.events.ChangeEventType;
import ir.map.gr222.sem7.utils.events.UserChangeEvent;
import ir.map.gr222.sem7.utils.observer.Observable;
import ir.map.gr222.sem7.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<UserChangeEvent> {
    UserDBRepository userDBRepository;
    MessageDBPagingRepository messageDBRepository;
    private List<Observer<UserChangeEvent>> observers=new ArrayList<>();

    public MessageService(UserDBRepository userDBRepository, MessageDBPagingRepository messageDBRepository) {
        this.userDBRepository = userDBRepository;
        this.messageDBRepository = messageDBRepository;
    }

    public Optional<Message> sendMessage(Message message){
        Optional<Message> m = this.messageDBRepository.save(message);
        if(m.isEmpty()){
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
        }
        return m;
    }

    public List<Message> getUserMessasges(Long userFromID, Long userToID){
        return this.messageDBRepository.findAllByUser(userFromID, userToID);
    }

    public Page<Message> getUserMessages(Pageable pageable, Long userFromID, Long userToID){
        return this.messageDBRepository.findAllByUser(pageable, userFromID, userToID);
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
