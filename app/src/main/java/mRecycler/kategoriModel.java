package mRecycler;


import com.ouayhan.ankara_yemek_yeni.R;


import java.util.ArrayList;


public class kategoriModel {

    private int kategoriResim;
    private String kategoriAd;
    private int quantity;
    private int kategoriIkon;


    public int getKategoriResim() {
        return kategoriResim;
    }

    public String getKategoriAd() {
        return kategoriAd;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getKategoriIkon() {
        return kategoriIkon;
    }

    public void setKategoriResim(int kategoriResim) {
        this.kategoriResim = kategoriResim;
    }

    public void setKategoriAd(String kategoriAd) {
        this.kategoriAd = kategoriAd;
    }

    public void setQuantity(int quan) {
        quantity = quan;
    }

    public void setKategoriIkon(int kategoriIkon) {
        this.kategoriIkon = kategoriIkon;
    }


    public static ArrayList<kategoriModel> getData() {

        ArrayList<kategoriModel> dataList = new ArrayList<>();

        int[] katResimler = {R.drawable.fish_resim, R.drawable.et_resim, R.drawable.mediter_resim, R.drawable.hambrg_resim, R.drawable.chicken_resim, R.drawable.sushi_resim};
        String[] katAd = {"Balık ve Deniz Ürünleri", "Kebap ve Et", "Vejeteryan", "Fast Food", "Tavuk Mekanları", "Sushi Mekanları"};
        int[] katIkon = {R.drawable.fish, R.drawable.ham_leg, R.drawable.salad, R.drawable.hamburger, R.drawable.chicken, R.drawable.sushi};


        for (int i = 0; i < katResimler.length; i++) {

            kategoriModel temp = new kategoriModel();


            temp.setKategoriResim(katResimler[i]);
            temp.setKategoriAd(katAd[i]);
            temp.setKategoriIkon(katIkon[i]);


            dataList.add(temp);
        }

        return dataList;

    }
}
