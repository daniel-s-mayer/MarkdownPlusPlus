public class Main {
    public static void main(String[] args) throws SyntaxException {
        String[] lines = {"+mySectOne", "+mySectTwo", "some text", ">mySectOne", ">mySectTwo"};
        System.out.println(new BaseChunkParser().parseChunk(null, lines));

    }
}