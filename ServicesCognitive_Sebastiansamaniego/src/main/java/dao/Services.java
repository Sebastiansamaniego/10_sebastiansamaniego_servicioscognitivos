package dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import modelo.Atributos;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Services {

    public Atributos ObtenerURL(String url) throws IOException {
        Atributos variablesModel = new Atributos();
        HttpClient httpClient = new DefaultHttpClient();
        try {
            StringEntity body = new StringEntity("{\"Url\": \"" + url + "\"}");
            JsonParser converter = new JsonParser();
            String emocion[] = "anger,contempt,disgust,fear,happiness,neutral,sadness,surprise".split(",");
            HttpPost request = new HttpPost("https://southcentralus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_01&returnRecognitionModel=false&returnFaceAttributes=hair,smile,headPose,gender,age,facialHair,glasses,makeup,emotion,occlusion,accessories,blur,exposure,noise");
            request.addHeader("Ocp-Apim-Subscription-Key", "79bf6234abc14c89a19ee675a4aa5f26");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(body);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            JsonArray array = converter.parse(EntityUtils.toString(entity)).getAsJsonArray();
            JsonObject object = array.get(0).getAsJsonObject();
            JsonObject attributes = object.getAsJsonObject("faceAttributes");
            JsonObject emotion = attributes.getAsJsonObject("emotion");
            JsonObject velloCara = attributes.getAsJsonObject("facialHair");
            JsonObject maquillaje = attributes.getAsJsonObject("makeup");
            JsonObject cabello = attributes.getAsJsonObject("hair");
            JsonArray colorCabello = cabello.getAsJsonArray("hairColor");
            String lentes = attributes.get("glasses").getAsString();
            String edad = attributes.get("age").getAsString();
            String genero = attributes.get("gender").getAsString();
            String nameEmotion = null;
            int valueEmotion = 0;
            for (String valores : emocion) {
                if (valueEmotion < emotion.get(valores).getAsInt()) {
                    nameEmotion = valores;
                    valueEmotion = emotion.get(valores).getAsInt();
                }
            }
            JsonObject colores = null;
            for (JsonElement jsonElement : colorCabello) {
                if (colores == null) {
                    colores = jsonElement.getAsJsonObject();
                } else if (colores.get("confidence").getAsDouble() < jsonElement.getAsJsonObject().get("confidence").getAsDouble()) {
                    colores = jsonElement.getAsJsonObject();
                }
            }
            String colorPelo = colores.get("color").getAsString();
//            System.out.println(json1);
            variablesModel.setEmotion(nameEmotion);
            variablesModel.setGlasses(lentes.equals("NoGlasses") ? "No tiene" : "Si tiene");
            variablesModel.setAge(edad);
            variablesModel.setGender(genero.equals("female") ? "Femenino" : "Masculino");            
            variablesModel.setHairColor(colorPelo);
            System.out.println("Emoción: " + nameEmotion);
            System.out.println("lentes: " + lentes);
            System.out.println("edad: " + edad);
            System.out.println("Género: " + genero);
            System.out.println("Color de cabello: " + colorPelo);
        } catch (IOException e) {
            throw e;
        }
        return variablesModel;
    }

}
