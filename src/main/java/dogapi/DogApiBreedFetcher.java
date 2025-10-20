package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();
    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = "https://dog.ceo/api/breeds/list/all";
        Request request = new Request.Builder().url(url).build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            JSONObject jo = new JSONObject(resp.body().string());
            JSONObject breeds = jo.getJSONObject("message");
            JSONArray subBreedsArr = breeds.getJSONArray(breed);
            List<String> subBreeds = new ArrayList<>();

            if (!breeds.has(breed)) {
                throw new BreedNotFoundException(breed);
            }

            for (int i = 0; i < subBreedsArr.length(); i++) {
                subBreeds.add(subBreedsArr.getString(i));
            }

            return subBreeds;

        } catch (Exception e) {
            throw new BreedNotFoundException(breed);
        }
    }
}