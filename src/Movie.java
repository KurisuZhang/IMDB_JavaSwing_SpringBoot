public class Movie {
    private String title;
    private String year;
    private String image;
    private String directors;
    private String stars;
    private String genres;
    private String imDbRating;


    public Movie(String title, String year, String image, String directors,
                 String stars, String genres, String imDbRating) {
        this.title = title;
        this.year = year;
        this.image = image;
        this.directors = directors;
        this.stars = stars;
        this.genres = genres;
        this.imDbRating = imDbRating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", image='" + image + '\'' +
                ", directors='" + directors + '\'' +
                ", stars='" + stars + '\'' +
                ", genres='" + genres + '\'' +
                ", imDbRating='" + imDbRating + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getImDbRating() {
        return imDbRating;
    }

    public void setImDbRating(String imDbRating) {
        this.imDbRating = imDbRating;
    }
}
