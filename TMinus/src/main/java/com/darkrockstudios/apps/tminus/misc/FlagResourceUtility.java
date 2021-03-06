package com.darkrockstudios.apps.tminus.misc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.darkrockstudios.apps.tminus.R;
import com.neovisionaries.i18n.CountryCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Adam on 2/16/14.
 */
public final class FlagResourceUtility
{
	private static Map<String, Integer> s_codeToFlag = new ConcurrentHashMap<>();

	static
	{
		s_codeToFlag.put( CountryCode.getByCode( "ad", false ).getAlpha3(), R.drawable.flag_ad );
		s_codeToFlag.put( CountryCode.getByCode( "ae", false ).getAlpha3(), R.drawable.flag_ae );
		s_codeToFlag.put( CountryCode.getByCode( "af", false ).getAlpha3(), R.drawable.flag_af );
		s_codeToFlag.put( CountryCode.getByCode( "ag", false ).getAlpha3(), R.drawable.flag_ag );
		s_codeToFlag.put( CountryCode.getByCode( "ai", false ).getAlpha3(), R.drawable.flag_ai );
		s_codeToFlag.put( CountryCode.getByCode( "al", false ).getAlpha3(), R.drawable.flag_al );
		s_codeToFlag.put( CountryCode.getByCode( "am", false ).getAlpha3(), R.drawable.flag_am );
		s_codeToFlag.put( CountryCode.getByCode( "ao", false ).getAlpha3(), R.drawable.flag_ao );
		s_codeToFlag.put( CountryCode.getByCode( "aq", false ).getAlpha3(), R.drawable.flag_aq );
		s_codeToFlag.put( CountryCode.getByCode( "ar", false ).getAlpha3(), R.drawable.flag_ar );
		s_codeToFlag.put( CountryCode.getByCode( "as", false ).getAlpha3(), R.drawable.flag_as );
		s_codeToFlag.put( CountryCode.getByCode( "at", false ).getAlpha3(), R.drawable.flag_at );
		s_codeToFlag.put( CountryCode.getByCode( "au", false ).getAlpha3(), R.drawable.flag_au );
		s_codeToFlag.put( CountryCode.getByCode( "aw", false ).getAlpha3(), R.drawable.flag_aw );
		s_codeToFlag.put( CountryCode.getByCode( "ax", false ).getAlpha3(), R.drawable.flag_ax );
		s_codeToFlag.put( CountryCode.getByCode( "az", false ).getAlpha3(), R.drawable.flag_az );
		s_codeToFlag.put( CountryCode.getByCode( "ba", false ).getAlpha3(), R.drawable.flag_ba );
		s_codeToFlag.put( CountryCode.getByCode( "bb", false ).getAlpha3(), R.drawable.flag_bb );
		s_codeToFlag.put( CountryCode.getByCode( "bd", false ).getAlpha3(), R.drawable.flag_bd );
		s_codeToFlag.put( CountryCode.getByCode( "be", false ).getAlpha3(), R.drawable.flag_be );
		s_codeToFlag.put( CountryCode.getByCode( "bf", false ).getAlpha3(), R.drawable.flag_bf );
		s_codeToFlag.put( CountryCode.getByCode( "bg", false ).getAlpha3(), R.drawable.flag_bg );
		s_codeToFlag.put( CountryCode.getByCode( "bh", false ).getAlpha3(), R.drawable.flag_bh );
		s_codeToFlag.put( CountryCode.getByCode( "bi", false ).getAlpha3(), R.drawable.flag_bi );
		s_codeToFlag.put( CountryCode.getByCode( "bj", false ).getAlpha3(), R.drawable.flag_bj );
		s_codeToFlag.put( CountryCode.getByCode( "bl", false ).getAlpha3(), R.drawable.flag_bl );
		s_codeToFlag.put( CountryCode.getByCode( "bm", false ).getAlpha3(), R.drawable.flag_bm );
		s_codeToFlag.put( CountryCode.getByCode( "bn", false ).getAlpha3(), R.drawable.flag_bn );
		s_codeToFlag.put( CountryCode.getByCode( "bo", false ).getAlpha3(), R.drawable.flag_bo );
		s_codeToFlag.put( CountryCode.getByCode( "bq", false ).getAlpha3(), R.drawable.flag_bq );
		s_codeToFlag.put( CountryCode.getByCode( "br", false ).getAlpha3(), R.drawable.flag_br );
		s_codeToFlag.put( CountryCode.getByCode( "bs", false ).getAlpha3(), R.drawable.flag_bs );
		s_codeToFlag.put( CountryCode.getByCode( "bt", false ).getAlpha3(), R.drawable.flag_bt );
		s_codeToFlag.put( CountryCode.getByCode( "bv", false ).getAlpha3(), R.drawable.flag_bv );
		s_codeToFlag.put( CountryCode.getByCode( "bw", false ).getAlpha3(), R.drawable.flag_bw );
		s_codeToFlag.put( CountryCode.getByCode( "by", false ).getAlpha3(), R.drawable.flag_by );
		s_codeToFlag.put( CountryCode.getByCode( "bz", false ).getAlpha3(), R.drawable.flag_bz );
		s_codeToFlag.put( CountryCode.getByCode( "ca", false ).getAlpha3(), R.drawable.flag_ca );
		s_codeToFlag.put( CountryCode.getByCode( "cc", false ).getAlpha3(), R.drawable.flag_cc );
		s_codeToFlag.put( CountryCode.getByCode( "cd", false ).getAlpha3(), R.drawable.flag_cd );
		s_codeToFlag.put( CountryCode.getByCode( "cf", false ).getAlpha3(), R.drawable.flag_cf );
		s_codeToFlag.put( CountryCode.getByCode( "cg", false ).getAlpha3(), R.drawable.flag_cg );
		s_codeToFlag.put( CountryCode.getByCode( "ch", false ).getAlpha3(), R.drawable.flag_ch );
		s_codeToFlag.put( CountryCode.getByCode( "ci", false ).getAlpha3(), R.drawable.flag_ci );
		s_codeToFlag.put( CountryCode.getByCode( "ck", false ).getAlpha3(), R.drawable.flag_ck );
		s_codeToFlag.put( CountryCode.getByCode( "cl", false ).getAlpha3(), R.drawable.flag_cl );
		s_codeToFlag.put( CountryCode.getByCode( "cm", false ).getAlpha3(), R.drawable.flag_cm );
		s_codeToFlag.put( CountryCode.getByCode( "cn", false ).getAlpha3(), R.drawable.flag_cn );
		s_codeToFlag.put( CountryCode.getByCode( "co", false ).getAlpha3(), R.drawable.flag_co );
		s_codeToFlag.put( CountryCode.getByCode( "co", false ).getAlpha3(), R.drawable.flag_co );
		s_codeToFlag.put( CountryCode.getByCode( "cr", false ).getAlpha3(), R.drawable.flag_cr );
		s_codeToFlag.put( CountryCode.getByCode( "cu", false ).getAlpha3(), R.drawable.flag_cu );
		s_codeToFlag.put( CountryCode.getByCode( "cv", false ).getAlpha3(), R.drawable.flag_cv );
		s_codeToFlag.put( CountryCode.getByCode( "cw", false ).getAlpha3(), R.drawable.flag_cw );
		s_codeToFlag.put( CountryCode.getByCode( "cx", false ).getAlpha3(), R.drawable.flag_cx );
		s_codeToFlag.put( CountryCode.getByCode( "cy", false ).getAlpha3(), R.drawable.flag_cy );
		s_codeToFlag.put( CountryCode.getByCode( "cz", false ).getAlpha3(), R.drawable.flag_cz );
		s_codeToFlag.put( CountryCode.getByCode( "de", false ).getAlpha3(), R.drawable.flag_de );
		s_codeToFlag.put( CountryCode.getByCode( "dj", false ).getAlpha3(), R.drawable.flag_dj );
		s_codeToFlag.put( CountryCode.getByCode( "dk", false ).getAlpha3(), R.drawable.flag_dk );
		s_codeToFlag.put( CountryCode.getByCode( "dm", false ).getAlpha3(), R.drawable.flag_dm );
		s_codeToFlag.put( CountryCode.getByCode( "do", false ).getAlpha3(), R.drawable.flag_do );
		s_codeToFlag.put( CountryCode.getByCode( "dz", false ).getAlpha3(), R.drawable.flag_dz );
		s_codeToFlag.put( CountryCode.getByCode( "ec", false ).getAlpha3(), R.drawable.flag_ec );
		s_codeToFlag.put( CountryCode.getByCode( "ee", false ).getAlpha3(), R.drawable.flag_ee );
		s_codeToFlag.put( CountryCode.getByCode( "eg", false ).getAlpha3(), R.drawable.flag_eg );
		s_codeToFlag.put( CountryCode.getByCode( "eh", false ).getAlpha3(), R.drawable.flag_eh );
		s_codeToFlag.put( CountryCode.getByCode( "er", false ).getAlpha3(), R.drawable.flag_er );
		s_codeToFlag.put( CountryCode.getByCode( "es", false ).getAlpha3(), R.drawable.flag_es );
		s_codeToFlag.put( CountryCode.getByCode( "et", false ).getAlpha3(), R.drawable.flag_et );
		s_codeToFlag.put( CountryCode.getByCode( "fi", false ).getAlpha3(), R.drawable.flag_fi );
		s_codeToFlag.put( CountryCode.getByCode( "fj", false ).getAlpha3(), R.drawable.flag_fj );
		s_codeToFlag.put( CountryCode.getByCode( "fk", false ).getAlpha3(), R.drawable.flag_fk );
		s_codeToFlag.put( CountryCode.getByCode( "fm", false ).getAlpha3(), R.drawable.flag_fm );
		s_codeToFlag.put( CountryCode.getByCode( "fo", false ).getAlpha3(), R.drawable.flag_fo );
		s_codeToFlag.put( CountryCode.getByCode( "fr", false ).getAlpha3(), R.drawable.flag_fr );
		s_codeToFlag.put( CountryCode.getByCode( "ga", false ).getAlpha3(), R.drawable.flag_ga );
		s_codeToFlag.put( CountryCode.getByCode( "gb", false ).getAlpha3(), R.drawable.flag_gb );
		s_codeToFlag.put( CountryCode.getByCode( "gd", false ).getAlpha3(), R.drawable.flag_gd );
		s_codeToFlag.put( CountryCode.getByCode( "ge", false ).getAlpha3(), R.drawable.flag_ge );
		s_codeToFlag.put( CountryCode.getByCode( "gf", false ).getAlpha3(), R.drawable.flag_gf );
		s_codeToFlag.put( CountryCode.getByCode( "gg", false ).getAlpha3(), R.drawable.flag_gg );
		s_codeToFlag.put( CountryCode.getByCode( "gh", false ).getAlpha3(), R.drawable.flag_gh );
		s_codeToFlag.put( CountryCode.getByCode( "gi", false ).getAlpha3(), R.drawable.flag_gi );
		s_codeToFlag.put( CountryCode.getByCode( "gl", false ).getAlpha3(), R.drawable.flag_gl );
		s_codeToFlag.put( CountryCode.getByCode( "gm", false ).getAlpha3(), R.drawable.flag_gm );
		s_codeToFlag.put( CountryCode.getByCode( "gn", false ).getAlpha3(), R.drawable.flag_gn );
		s_codeToFlag.put( CountryCode.getByCode( "gp", false ).getAlpha3(), R.drawable.flag_gp );
		s_codeToFlag.put( CountryCode.getByCode( "gq", false ).getAlpha3(), R.drawable.flag_gq );
		s_codeToFlag.put( CountryCode.getByCode( "gr", false ).getAlpha3(), R.drawable.flag_gr );
		s_codeToFlag.put( CountryCode.getByCode( "gs", false ).getAlpha3(), R.drawable.flag_gs );
		s_codeToFlag.put( CountryCode.getByCode( "gt", false ).getAlpha3(), R.drawable.flag_gt );
		s_codeToFlag.put( CountryCode.getByCode( "gu", false ).getAlpha3(), R.drawable.flag_gu );
		s_codeToFlag.put( CountryCode.getByCode( "gw", false ).getAlpha3(), R.drawable.flag_gw );
		s_codeToFlag.put( CountryCode.getByCode( "gy", false ).getAlpha3(), R.drawable.flag_gy );
		s_codeToFlag.put( CountryCode.getByCode( "hk", false ).getAlpha3(), R.drawable.flag_hk );
		s_codeToFlag.put( CountryCode.getByCode( "hm", false ).getAlpha3(), R.drawable.flag_hm );
		s_codeToFlag.put( CountryCode.getByCode( "hn", false ).getAlpha3(), R.drawable.flag_hn );
		s_codeToFlag.put( CountryCode.getByCode( "hr", false ).getAlpha3(), R.drawable.flag_hr );
		s_codeToFlag.put( CountryCode.getByCode( "ht", false ).getAlpha3(), R.drawable.flag_ht );
		s_codeToFlag.put( CountryCode.getByCode( "hu", false ).getAlpha3(), R.drawable.flag_hu );
		s_codeToFlag.put( CountryCode.getByCode( "id", false ).getAlpha3(), R.drawable.flag_id );
		s_codeToFlag.put( CountryCode.getByCode( "ie", false ).getAlpha3(), R.drawable.flag_ie );
		s_codeToFlag.put( CountryCode.getByCode( "il", false ).getAlpha3(), R.drawable.flag_il );
		s_codeToFlag.put( CountryCode.getByCode( "im", false ).getAlpha3(), R.drawable.flag_im );
		s_codeToFlag.put( CountryCode.getByCode( "in", false ).getAlpha3(), R.drawable.flag_in );
		s_codeToFlag.put( CountryCode.getByCode( "io", false ).getAlpha3(), R.drawable.flag_io );
		s_codeToFlag.put( CountryCode.getByCode( "iq", false ).getAlpha3(), R.drawable.flag_iq );
		s_codeToFlag.put( CountryCode.getByCode( "ir", false ).getAlpha3(), R.drawable.flag_ir );
		s_codeToFlag.put( CountryCode.getByCode( "is", false ).getAlpha3(), R.drawable.flag_is );
		s_codeToFlag.put( CountryCode.getByCode( "it", false ).getAlpha3(), R.drawable.flag_it );
		s_codeToFlag.put( CountryCode.getByCode( "je", false ).getAlpha3(), R.drawable.flag_je );
		s_codeToFlag.put( CountryCode.getByCode( "jm", false ).getAlpha3(), R.drawable.flag_jm );
		s_codeToFlag.put( CountryCode.getByCode( "jo", false ).getAlpha3(), R.drawable.flag_jo );
		s_codeToFlag.put( CountryCode.getByCode( "jp", false ).getAlpha3(), R.drawable.flag_jp );
		s_codeToFlag.put( CountryCode.getByCode( "ke", false ).getAlpha3(), R.drawable.flag_ke );
		s_codeToFlag.put( CountryCode.getByCode( "kg", false ).getAlpha3(), R.drawable.flag_kg );
		s_codeToFlag.put( CountryCode.getByCode( "kh", false ).getAlpha3(), R.drawable.flag_kh );
		s_codeToFlag.put( CountryCode.getByCode( "ki", false ).getAlpha3(), R.drawable.flag_ki );
		s_codeToFlag.put( CountryCode.getByCode( "km", false ).getAlpha3(), R.drawable.flag_km );
		s_codeToFlag.put( CountryCode.getByCode( "kn", false ).getAlpha3(), R.drawable.flag_kn );
		s_codeToFlag.put( CountryCode.getByCode( "kp", false ).getAlpha3(), R.drawable.flag_kp );
		s_codeToFlag.put( CountryCode.getByCode( "kr", false ).getAlpha3(), R.drawable.flag_kr );
		s_codeToFlag.put( CountryCode.getByCode( "kw", false ).getAlpha3(), R.drawable.flag_kw );
		s_codeToFlag.put( CountryCode.getByCode( "ky", false ).getAlpha3(), R.drawable.flag_ky );
		s_codeToFlag.put( CountryCode.getByCode( "kz", false ).getAlpha3(), R.drawable.flag_kz );
		s_codeToFlag.put( CountryCode.getByCode( "la", false ).getAlpha3(), R.drawable.flag_la );
		s_codeToFlag.put( CountryCode.getByCode( "lb", false ).getAlpha3(), R.drawable.flag_lb );
		s_codeToFlag.put( CountryCode.getByCode( "lc", false ).getAlpha3(), R.drawable.flag_lc );
		s_codeToFlag.put( CountryCode.getByCode( "li", false ).getAlpha3(), R.drawable.flag_li );
		s_codeToFlag.put( CountryCode.getByCode( "lk", false ).getAlpha3(), R.drawable.flag_lk );
		s_codeToFlag.put( CountryCode.getByCode( "lr", false ).getAlpha3(), R.drawable.flag_lr );
		s_codeToFlag.put( CountryCode.getByCode( "ls", false ).getAlpha3(), R.drawable.flag_ls );
		s_codeToFlag.put( CountryCode.getByCode( "lt", false ).getAlpha3(), R.drawable.flag_lt );
		s_codeToFlag.put( CountryCode.getByCode( "lu", false ).getAlpha3(), R.drawable.flag_lu );
		s_codeToFlag.put( CountryCode.getByCode( "lv", false ).getAlpha3(), R.drawable.flag_lv );
		s_codeToFlag.put( CountryCode.getByCode( "ly", false ).getAlpha3(), R.drawable.flag_ly );
		s_codeToFlag.put( CountryCode.getByCode( "ma", false ).getAlpha3(), R.drawable.flag_ma );
		s_codeToFlag.put( CountryCode.getByCode( "mc", false ).getAlpha3(), R.drawable.flag_mc );
		s_codeToFlag.put( CountryCode.getByCode( "md", false ).getAlpha3(), R.drawable.flag_md );
		s_codeToFlag.put( CountryCode.getByCode( "me", false ).getAlpha3(), R.drawable.flag_me );
		s_codeToFlag.put( CountryCode.getByCode( "mf", false ).getAlpha3(), R.drawable.flag_mf );
		s_codeToFlag.put( CountryCode.getByCode( "mg", false ).getAlpha3(), R.drawable.flag_mg );
		s_codeToFlag.put( CountryCode.getByCode( "mh", false ).getAlpha3(), R.drawable.flag_mh );
		s_codeToFlag.put( CountryCode.getByCode( "mk", false ).getAlpha3(), R.drawable.flag_mk );
		s_codeToFlag.put( CountryCode.getByCode( "ml", false ).getAlpha3(), R.drawable.flag_ml );
		s_codeToFlag.put( CountryCode.getByCode( "mm", false ).getAlpha3(), R.drawable.flag_mm );
		s_codeToFlag.put( CountryCode.getByCode( "mn", false ).getAlpha3(), R.drawable.flag_mn );
		s_codeToFlag.put( CountryCode.getByCode( "mo", false ).getAlpha3(), R.drawable.flag_mo );
		s_codeToFlag.put( CountryCode.getByCode( "mp", false ).getAlpha3(), R.drawable.flag_mp );
		s_codeToFlag.put( CountryCode.getByCode( "mq", false ).getAlpha3(), R.drawable.flag_mq );
		s_codeToFlag.put( CountryCode.getByCode( "mr", false ).getAlpha3(), R.drawable.flag_mr );
		s_codeToFlag.put( CountryCode.getByCode( "ms", false ).getAlpha3(), R.drawable.flag_ms );
		s_codeToFlag.put( CountryCode.getByCode( "mt", false ).getAlpha3(), R.drawable.flag_mt );
		s_codeToFlag.put( CountryCode.getByCode( "mu", false ).getAlpha3(), R.drawable.flag_mu );
		s_codeToFlag.put( CountryCode.getByCode( "mv", false ).getAlpha3(), R.drawable.flag_mv );
		s_codeToFlag.put( CountryCode.getByCode( "mw", false ).getAlpha3(), R.drawable.flag_mw );
		s_codeToFlag.put( CountryCode.getByCode( "mx", false ).getAlpha3(), R.drawable.flag_mx );
		s_codeToFlag.put( CountryCode.getByCode( "my", false ).getAlpha3(), R.drawable.flag_my );
		s_codeToFlag.put( CountryCode.getByCode( "mz", false ).getAlpha3(), R.drawable.flag_mz );
		s_codeToFlag.put( CountryCode.getByCode( "na", false ).getAlpha3(), R.drawable.flag_na );
		s_codeToFlag.put( CountryCode.getByCode( "nc", false ).getAlpha3(), R.drawable.flag_nc );
		s_codeToFlag.put( CountryCode.getByCode( "ne", false ).getAlpha3(), R.drawable.flag_ne );
		s_codeToFlag.put( CountryCode.getByCode( "nf", false ).getAlpha3(), R.drawable.flag_nf );
		s_codeToFlag.put( CountryCode.getByCode( "ng", false ).getAlpha3(), R.drawable.flag_ng );
		s_codeToFlag.put( CountryCode.getByCode( "ni", false ).getAlpha3(), R.drawable.flag_ni );
		s_codeToFlag.put( CountryCode.getByCode( "nl", false ).getAlpha3(), R.drawable.flag_nl );
		s_codeToFlag.put( CountryCode.getByCode( "no", false ).getAlpha3(), R.drawable.flag_no );
		s_codeToFlag.put( CountryCode.getByCode( "np", false ).getAlpha3(), R.drawable.flag_np );
		s_codeToFlag.put( CountryCode.getByCode( "nr", false ).getAlpha3(), R.drawable.flag_nr );
		s_codeToFlag.put( CountryCode.getByCode( "nu", false ).getAlpha3(), R.drawable.flag_nu );
		s_codeToFlag.put( CountryCode.getByCode( "nz", false ).getAlpha3(), R.drawable.flag_nz );
		s_codeToFlag.put( CountryCode.getByCode( "om", false ).getAlpha3(), R.drawable.flag_om );
		s_codeToFlag.put( CountryCode.getByCode( "pa", false ).getAlpha3(), R.drawable.flag_pa );
		s_codeToFlag.put( CountryCode.getByCode( "pe", false ).getAlpha3(), R.drawable.flag_pe );
		s_codeToFlag.put( CountryCode.getByCode( "pf", false ).getAlpha3(), R.drawable.flag_pf );
		s_codeToFlag.put( CountryCode.getByCode( "pg", false ).getAlpha3(), R.drawable.flag_pg );
		s_codeToFlag.put( CountryCode.getByCode( "ph", false ).getAlpha3(), R.drawable.flag_ph );
		s_codeToFlag.put( CountryCode.getByCode( "pk", false ).getAlpha3(), R.drawable.flag_pk );
		s_codeToFlag.put( CountryCode.getByCode( "pl", false ).getAlpha3(), R.drawable.flag_pl );
		s_codeToFlag.put( CountryCode.getByCode( "pm", false ).getAlpha3(), R.drawable.flag_pm );
		s_codeToFlag.put( CountryCode.getByCode( "pn", false ).getAlpha3(), R.drawable.flag_pn );
		s_codeToFlag.put( CountryCode.getByCode( "pr", false ).getAlpha3(), R.drawable.flag_pr );
		s_codeToFlag.put( CountryCode.getByCode( "pr", false ).getAlpha3(), R.drawable.flag_pr );
		s_codeToFlag.put( CountryCode.getByCode( "ps", false ).getAlpha3(), R.drawable.flag_ps );
		s_codeToFlag.put( CountryCode.getByCode( "pt", false ).getAlpha3(), R.drawable.flag_pt );
		s_codeToFlag.put( CountryCode.getByCode( "pw", false ).getAlpha3(), R.drawable.flag_pw );
		s_codeToFlag.put( CountryCode.getByCode( "py", false ).getAlpha3(), R.drawable.flag_py );
		s_codeToFlag.put( CountryCode.getByCode( "qa", false ).getAlpha3(), R.drawable.flag_qa );
		s_codeToFlag.put( CountryCode.getByCode( "re", false ).getAlpha3(), R.drawable.flag_re );
		s_codeToFlag.put( CountryCode.getByCode( "ro", false ).getAlpha3(), R.drawable.flag_ro );
		s_codeToFlag.put( CountryCode.getByCode( "rs", false ).getAlpha3(), R.drawable.flag_rs );
		s_codeToFlag.put( CountryCode.getByCode( "ru", false ).getAlpha3(), R.drawable.flag_ru );
		s_codeToFlag.put( CountryCode.getByCode( "rw", false ).getAlpha3(), R.drawable.flag_rw );
		s_codeToFlag.put( CountryCode.getByCode( "sa", false ).getAlpha3(), R.drawable.flag_sa );
		s_codeToFlag.put( CountryCode.getByCode( "sb", false ).getAlpha3(), R.drawable.flag_sb );
		s_codeToFlag.put( CountryCode.getByCode( "sc", false ).getAlpha3(), R.drawable.flag_sc );
		s_codeToFlag.put( CountryCode.getByCode( "sd", false ).getAlpha3(), R.drawable.flag_sd );
		s_codeToFlag.put( CountryCode.getByCode( "se", false ).getAlpha3(), R.drawable.flag_se );
		s_codeToFlag.put( CountryCode.getByCode( "sg", false ).getAlpha3(), R.drawable.flag_sg );
		s_codeToFlag.put( CountryCode.getByCode( "sh", false ).getAlpha3(), R.drawable.flag_sh );
		s_codeToFlag.put( CountryCode.getByCode( "si", false ).getAlpha3(), R.drawable.flag_si );
		s_codeToFlag.put( CountryCode.getByCode( "sj", false ).getAlpha3(), R.drawable.flag_sj );
		s_codeToFlag.put( CountryCode.getByCode( "sk", false ).getAlpha3(), R.drawable.flag_sk );
		s_codeToFlag.put( CountryCode.getByCode( "sl", false ).getAlpha3(), R.drawable.flag_sl );
		s_codeToFlag.put( CountryCode.getByCode( "sm", false ).getAlpha3(), R.drawable.flag_sm );
		s_codeToFlag.put( CountryCode.getByCode( "sn", false ).getAlpha3(), R.drawable.flag_sn );
		s_codeToFlag.put( CountryCode.getByCode( "so", false ).getAlpha3(), R.drawable.flag_so );
		s_codeToFlag.put( CountryCode.getByCode( "sr", false ).getAlpha3(), R.drawable.flag_sr );
		s_codeToFlag.put( CountryCode.getByCode( "ss", false ).getAlpha3(), R.drawable.flag_ss );
		s_codeToFlag.put( CountryCode.getByCode( "st", false ).getAlpha3(), R.drawable.flag_st );
		s_codeToFlag.put( CountryCode.getByCode( "sv", false ).getAlpha3(), R.drawable.flag_sv );
		s_codeToFlag.put( CountryCode.getByCode( "sx", false ).getAlpha3(), R.drawable.flag_sx );
		s_codeToFlag.put( CountryCode.getByCode( "sy", false ).getAlpha3(), R.drawable.flag_sy );
		s_codeToFlag.put( CountryCode.getByCode( "sz", false ).getAlpha3(), R.drawable.flag_sz );
		s_codeToFlag.put( CountryCode.getByCode( "tc", false ).getAlpha3(), R.drawable.flag_tc );
		s_codeToFlag.put( CountryCode.getByCode( "td", false ).getAlpha3(), R.drawable.flag_td );
		s_codeToFlag.put( CountryCode.getByCode( "tf", false ).getAlpha3(), R.drawable.flag_tf );
		s_codeToFlag.put( CountryCode.getByCode( "tg", false ).getAlpha3(), R.drawable.flag_tg );
		s_codeToFlag.put( CountryCode.getByCode( "th", false ).getAlpha3(), R.drawable.flag_th );
		s_codeToFlag.put( CountryCode.getByCode( "tj", false ).getAlpha3(), R.drawable.flag_tj );
		s_codeToFlag.put( CountryCode.getByCode( "tk", false ).getAlpha3(), R.drawable.flag_tk );
		s_codeToFlag.put( CountryCode.getByCode( "tl", false ).getAlpha3(), R.drawable.flag_tl );
		s_codeToFlag.put( CountryCode.getByCode( "tm", false ).getAlpha3(), R.drawable.flag_tm );
		s_codeToFlag.put( CountryCode.getByCode( "tn", false ).getAlpha3(), R.drawable.flag_tn );
		s_codeToFlag.put( CountryCode.getByCode( "to", false ).getAlpha3(), R.drawable.flag_to );
		s_codeToFlag.put( CountryCode.getByCode( "tr", false ).getAlpha3(), R.drawable.flag_tr );
		s_codeToFlag.put( CountryCode.getByCode( "tt", false ).getAlpha3(), R.drawable.flag_tt );
		s_codeToFlag.put( CountryCode.getByCode( "tv", false ).getAlpha3(), R.drawable.flag_tv );
		s_codeToFlag.put( CountryCode.getByCode( "tw", false ).getAlpha3(), R.drawable.flag_tw );
		s_codeToFlag.put( CountryCode.getByCode( "tz", false ).getAlpha3(), R.drawable.flag_tz );
		s_codeToFlag.put( CountryCode.getByCode( "ua", false ).getAlpha3(), R.drawable.flag_ua );
		s_codeToFlag.put( CountryCode.getByCode( "ug", false ).getAlpha3(), R.drawable.flag_ug );
		s_codeToFlag.put( CountryCode.getByCode( "um", false ).getAlpha3(), R.drawable.flag_um );
		s_codeToFlag.put( CountryCode.getByCode( "us", false ).getAlpha3(), R.drawable.flag_us );
		s_codeToFlag.put( CountryCode.getByCode( "uy", false ).getAlpha3(), R.drawable.flag_uy );
		s_codeToFlag.put( CountryCode.getByCode( "uz", false ).getAlpha3(), R.drawable.flag_uz );
		s_codeToFlag.put( CountryCode.getByCode( "va", false ).getAlpha3(), R.drawable.flag_va );
		s_codeToFlag.put( CountryCode.getByCode( "vc", false ).getAlpha3(), R.drawable.flag_vc );
		s_codeToFlag.put( CountryCode.getByCode( "ve", false ).getAlpha3(), R.drawable.flag_ve );
		s_codeToFlag.put( CountryCode.getByCode( "vg", false ).getAlpha3(), R.drawable.flag_vg );
		s_codeToFlag.put( CountryCode.getByCode( "vi", false ).getAlpha3(), R.drawable.flag_vi );
		s_codeToFlag.put( CountryCode.getByCode( "vn", false ).getAlpha3(), R.drawable.flag_vn );
		s_codeToFlag.put( CountryCode.getByCode( "vu", false ).getAlpha3(), R.drawable.flag_vu );
		s_codeToFlag.put( CountryCode.getByCode( "wf", false ).getAlpha3(), R.drawable.flag_wf );
		s_codeToFlag.put( CountryCode.getByCode( "ws", false ).getAlpha3(), R.drawable.flag_ws );
		s_codeToFlag.put( CountryCode.getByCode( "ye", false ).getAlpha3(), R.drawable.flag_ye );
		s_codeToFlag.put( CountryCode.getByCode( "yt", false ).getAlpha3(), R.drawable.flag_yt );
		s_codeToFlag.put( CountryCode.getByCode( "za", false ).getAlpha3(), R.drawable.flag_za );
		s_codeToFlag.put( CountryCode.getByCode( "zm", false ).getAlpha3(), R.drawable.flag_zm );
		s_codeToFlag.put( CountryCode.getByCode( "zw", false ).getAlpha3(), R.drawable.flag_zw );
	}

	public static Drawable getFlagDrawable( final String cslCountryCodes, final Context context )
	{
		final Drawable flagDrawable;

		Resources resources = context.getResources();

		if( cslCountryCodes.contains( "," ) )
		{
			String[] countryCodes = cslCountryCodes.split( "," );
			if( countryCodes != null && countryCodes.length > 0 )
			{
				Drawable[] flagResources = new Drawable[ countryCodes.length ];
				for( int ii = 0; ii < countryCodes.length; ++ii )
				{
					flagResources[ ii ] = resources.getDrawable( getFlagResource( countryCodes[ ii ] ) );
				}

				CyclicTransitionDrawable transitionDrawable = new CyclicTransitionDrawable( flagResources );

				final int transitionDuration = resources.getInteger( R.integer.transition_duration_ms );
				final int transitionPause = resources.getInteger( R.integer.transition_pause_ms );
				transitionDrawable.startTransition( transitionDuration, transitionPause );

				flagDrawable = transitionDrawable;
			}
			else
			{
				int flagResource = getFlagResource( cslCountryCodes );
				flagDrawable = resources.getDrawable( flagResource );
			}
		}
		else
		{
			int flagResource = getFlagResource( cslCountryCodes );
			flagDrawable = resources.getDrawable( flagResource );
		}

		return flagDrawable;
	}

	// Takes in the ISO 3166-1 alpha-3 country code
	public static int getFlagResource( final String countryCode )
	{
		int resourceId = R.drawable.flag_unknown;

		if( countryCode != null && countryCode.trim().length() > 0 )
		{
			CountryCode cc = CountryCode.getByCode( countryCode, false );
			if( cc != null )
			{
				resourceId = s_codeToFlag.get( cc.getAlpha3() );
			}
		}

		return resourceId;
	}
}
