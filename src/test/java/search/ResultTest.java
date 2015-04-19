package search;

import com.search.Result;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * User: YamStranger
 * Date: 4/16/15
 * Time: 7:57 PM
 */
public class ResultTest {
    @Test
    public void get_maxMin_proceed() {
        Result result = new Result();
        Result.Repository repository = new Result.Repository();
        repository.contributors = 1;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 2;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 3;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 4;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 5;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 6;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 7;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 8;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 9;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 10;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 11;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 12;
        result.addRepository(repository);
        repository = new Result.Repository();
        repository.contributors = 13;
        result.addRepository(repository);

        int contributors = 13;
        for (final Result.Repository current : result.get(10)) {
            Assert.assertEquals(current.contributors, contributors--);
        }
    }
}
