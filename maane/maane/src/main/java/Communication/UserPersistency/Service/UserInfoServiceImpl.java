package Communication.UserPersistency.Service;

import Communication.UserPersistency.Entity.UserInfo;
import Domain.CommonClasses.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserInfoServiceImpl implements UserInfoService, UserDetailsService {
    private final Map<String, Pair<String, String>> repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pair<String, String> data = repo.get(username);

        if(data == null){
            log.error("user not found in db");
            throw new UsernameNotFoundException("user not found");
        }
        else{
          log.info("user found in db: {}", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(data.getSecond()));

        return new org.springframework.security.core.userdetails.User(username, data.getFirst(), authorities);
    }

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));

        log.info("user {} saved in the connection db with pwd {}", userInfo.getUsername(), userInfo.getPassword());

        repo.put(userInfo.getUsername(), new Pair<>(userInfo.getPassword(), userInfo.getRole()));
        return userInfo;
    }

    @Override
    public UserInfo getUser(String username) {

        Pair<String, String> data = repo.get(username);
        return new UserInfo(username, data.getFirst(), data.getSecond());
    }

}
