package com.poidemo.entity;
import com.poidemo.PoidemoApplicationTests;
import com.poidemo.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by AZ
 */

public class UserRepositoryTest extends PoidemoApplicationTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindById() {
        User user = userRepository.findById(1L).get();
        Assert.assertEquals("az", user.getName());
    }
}
