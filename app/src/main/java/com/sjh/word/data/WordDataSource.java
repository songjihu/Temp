package com.sjh.word.data;

import com.sjh.word.data.model.LoggedInUser;
import com.sjh.word.data.model.WordModel;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class WordDataSource {

    public Result<WordModel> update(String test1, String test2,String test3,String test4) {

        try {
            WordModel fakeWord =
                    new WordModel(
                            "001",
                            "1",
                            "abandon",
                            "放弃");
            return new Result.Success<>(fakeWord);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error update", e));
        }
    }

}
