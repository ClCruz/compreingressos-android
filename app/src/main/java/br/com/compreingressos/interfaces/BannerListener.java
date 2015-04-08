package br.com.compreingressos.interfaces;

import java.util.ArrayList;

import br.com.compreingressos.model.Banner;

/**
 * Created by luiszacheu on 08/04/15.
 */
public interface BannerListener {
    public void updateBannerAdapter(ArrayList<Banner> listBanner);
}
