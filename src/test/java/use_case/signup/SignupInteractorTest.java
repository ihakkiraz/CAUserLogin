
package use_case.signup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import data_access.InMemoryUserDataAccessObject;
import entity.CommonUserFactory;
import entity.User;
import entity.UserFactory;

public class SignupInteractorTest {

    @Test
    public void successTest() {
        final SignupInputData inputData = new SignupInputData("Paul", "password", "password");
        final SignupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // This creates a successPresenter that tests whether the test case is as we expect.
        final SignupOutputBoundary successPresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData user) {
                // 2 things to check: the output data is correct, and the user has been created in the DAO.
                assertEquals("Paul", user.getUsername());
                assertTrue(userRepository.existsByName("Paul"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }

            @Override
            public void switchToLoginView() {
                // This is expected
            }
        };

        final SignupInputBoundary interactor =
                new SignupInteractor(userRepository, successPresenter, new CommonUserFactory());
        interactor.execute(inputData);
    }

    @Test
    public void failurePasswordMismatchTest() {
        final SignupInputData inputData = new SignupInputData("Paul", "password", "wrong");
        final SignupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // This creates a presenter that tests whether the test case is as we expect.
        final SignupOutputBoundary failurePresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData user) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Passwords don't match.", error);
            }

            @Override
            public void switchToLoginView() {
                // This is expected
            }
        };

        final SignupInputBoundary interactor =
                new SignupInteractor(userRepository, failurePresenter, new CommonUserFactory());
        interactor.execute(inputData);
    }

    @Test
    public void failureUserExistsTest() {
        final SignupInputData inputData =
                new SignupInputData("Paul", "password", "wrong");
        final SignupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // Add Paul to the repo so that when we check later they already exist
        final UserFactory factory = new CommonUserFactory();
        final User user = factory.create("Paul", "pwd");
        userRepository.save(user);

        // This creates a presenter that tests whether the test case is as we expect.
        final SignupOutputBoundary failurePresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData user) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("User already exists.", error);
            }

            @Override
            public void switchToLoginView() {
                // This is expected
            }
        };

        final SignupInputBoundary interactor =
                new SignupInteractor(userRepository, failurePresenter, new CommonUserFactory());
        interactor.execute(inputData);
    }
}
