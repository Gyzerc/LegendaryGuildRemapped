package com.gyzer.Configurations;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.MsgUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Language extends FileProvider {
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    public String reset_user_cooldown;
    public String icon_not_exist;
    public Language(String nation) {
        super(nation+".yml", "Lang/", "./plugins/LegendaryGuildRemapped/Lang");
    }


    public String plugin;
    public String unknown_command;
    public String nopermission;
    public String already_in_guild;
    public String notmath;
    public String notguild;
    public String notplayer;
    public String nothasguild;
    public String notowner;
    public String notmember;
    public String max_length_intro;
    public String max_length_notice;
    public String vault_noenough;
    public String no_guildmoney;
    public String noenough_level;
    public String noenough_treelevel;
    public String noenough_members;
    public String noenough_guildpoints;
    public String nopass_position;
    public String nopass_chance;
    public String no_panel;
    public String input;
    public String isowner;
    public String member_max;
    public List<String> help_player;
    public List<String> help_admin;
    public String uset_recive_points;
    public String uset_decrease_points;
    public String uset_set_points;
    public String user_set_position;
    public String admin_target_nothasguild;
    public String admin_give_points;
    public String admin_take_points;
    public String admin_set_points;
    public String admin_set_position_null;
    public String admin_set_position;
    public String admin_set_position_cant;
    public String admin_set_position_cant_owner;
    public String admin_set_position_cant_max;
    public String admin_give_money;
    public String admin_take_money;
    public String admin_set_money;
    public String admin_level_add;
    public String admin_level_take;
    public String admin_level_set;
    public String admin_exp_add;
    public String admin_exp_take;
    public String admin_exp_set;
    public String admin_treeexp_add;
    public String admin_treeexp_take;
    public String admin_treeexp_set;
    public String admin_treelevel_add;
    public String admin_treelevel_set;
    public String admin_treelevel_take;
    public String admin_bufflevel_add;
    public String admin_bufflevel_take;
    public String admin_bufflevel_set;
    public String admin_buff_null;
    public String admin_activity_add;
    public String admin_activity_take;
    public String admin_activity_set;
    public String admin_maxmembers_add;
    public String admin_maxmembers_set;
    public String admin_teamshop_refresh;
    public String admin_add_icon;
    public String admin_remove_icon;
    public String admin_add_member;
    public String admin_position;
    public String default_null;
    public String default_guild;
    public String default_position;
    public String default_intro;
    public String default_notice;
    public String create_cooldown;
    public String create_length;
    public String create_exists;
    public String create_message;
    public List<String> create_broad;
    public String reuirement_notenough_vault;
    public String reuirement_notenough_playerpoints;
    public String requirement_notenough_item;
    public String requirement_notenough_activity;
    public String requirement_notenough_total_activity;
    public String application_send;
    public String application_recive;
    public String application_already;
    public String application_already_in_guild;
    public String application_wait;
    public String application_pass;
    public String application_join;
    public String application_deny;
    public String application_deny_target;
    public String application_join_broad;
    public String members_kick;
    public String members_kick_broad;
    public String members_bekick;
    public String stores_unlock;
    public String stores_unlock_broad;
    public String stores_has_used;
    public String stores_add_white;
    public String stores_add_white_already;
    public String stores_remove_white;
    public String stores_remove_white_null;
    public String stores_add_white_title;
    public String stores_remove_white_title;
    public String stores_cant_use;
    public String stores_cant_input;
    public String redpacket_min_total;
    public String redpacket_min_amount;
    public String redpacket_create;
    public String redpacket_create_broad;
    public String redpacket_garb_no;
    public String redpacket_garb_already;
    public String redpacket_garb;
    public String redpacket_garb_finally;
    public String redpacket_create_amount_max;
    public String level_levelup;
    public List<String> level_levelup_broad;
    public String level_expadd;
    public String tree_levelup;
    public String tree_levelup_cant;
    public String tree_levelup_byplayer;
    public String tree_level_large;
    public String tree_level_max;
    public String tree_expadd_byplayer;
    public String tree_wish_refresh;
    public String tree_wish;
    public String tree_wish_broad;
    public String tree_wish_already;
    public String tree_water_refresh;
    public String tree_water;
    public String tree_water_broad;
    public String tree_water_limit;
    public String icon_unlock;
    public String icon_locked;
    public String icon_put;
    public String shop_buy;
    public String shop_limit;
    public String shop_refresh_day;
    public String shop_refresh_week;
    public String shop_refresh_month;
    public String buff_levelup;
    public String buff_max;
    public String buff_cant;
    public String pvp_enable;
    public String pvp_disable;
    public String pvp_cant;
    public String pvp_cant_target;
    public String quit_message;
    public String quit_broad;
    public String quit_owner;
    public String delete_confirm;
    public String delete_message;
    public String delete_broad_members;
    public List<String> delete_broad;
    public String give_message;
    public String give_message_target;
    public String give_broad;
    public String activity_gain;
    public String activity_join_tip;
    public String activity_claim;
    public String activity_cant_claim;
    public String activity_already_claimed;
    public String positions_add_write;
    public String positions_remove_write;
    public String positions_max;
    public String positions_message;
    public String positions_message_target;
    public String positions_message_cancel;
    public String positions_message_cancel_target;
    public String money_message;
    public String money_message_broad;
    public String home_home_null;
    public String home_set;
    public String home_set_broad;
    public String home_wait;
    public String home_teleport;
    public String home_cancel;
    public String home_cant_world;
    public String home_cant_server;
    public String intro_add;
    public String intro_remove;
    public String notice_add;
    public String notice_remove;
    public String chat_enable;
    public String chat_disable;
    public String reset_activity;
    public String reset_shop;
    public String reset_wish;
    public String reset_pot;
    public String reset_guild_teamshop;
    public String reset_user_teamshop;
    public String bargain_already;
    public String bargain_success;
    public String bargain_bargain_broad;
    public String bargain_buy;
    public String bargain_buy_broad;
    public String bargain_buy_limit;


    @Override
    public void readDefault() {

        this.plugin = MsgUtils.color(getValue("plugin","&b&lLegendary&3&lGuild"));
        this.unknown_command = MsgUtils.color(getValue("unknown_command","&f该指令不存在或格式出错！"));
        this.nopermission = MsgUtils.color(getValue("nopermission","&f你没有该权限！"));
        this.already_in_guild = MsgUtils.color(getValue("already_in_guild","&f你已经在一个公会内了"));
        this.nothasguild = MsgUtils.color(getValue("nothasguild","&f你不在一个公会内！"));
        this.notowner = MsgUtils.color(getValue("notowner","&f你不是会长！"));
        this.notmember = MsgUtils.color(getValue("notmember","&f公会内没有该成员"));
        this.no_guildmoney = MsgUtils.color(getValue("no_guildmoney","&f公会资金不足 &e%value%"));
        max_length_intro = MsgUtils.color(getValue("max-length-intro","&fGuild introductions can only have a maximum of 5 entries！"));
        max_length_notice = MsgUtils.color(getValue("max-length-notice","&fGuild notice can only have a maximum of 5 articles！"));
        this.noenough_level = MsgUtils.color(getValue("noenough_level","&f公会等级不足 &e%value%"));
        this.noenough_treelevel = MsgUtils.color(getValue("noenough_treelevel","&f公会神树等级不足 &e%value%"));
        this.noenough_members = MsgUtils.color(getValue("noenough_members","&f公会成员人数不足 &e%value%"));
        this.noenough_guildpoints = MsgUtils.color(getValue("noenough_guildpoints","&f你的公会贡献值不足 &e%value%"));
        this.nopass_position = MsgUtils.color(getValue("nopass_position","&c你的公会职位不允许进行该操作."));
        this.nopass_chance = MsgUtils.color(getValue("nopass_chance","&c你的运气似乎不太好..."));
        this.no_panel = MsgUtils.color(getValue("no_panel","&f没有该界面."));
        this.input = MsgUtils.color(getValue("input","&f请在聊天框中发送内容，输入 &c'cancel' &f即可取消."));
        this.vault_noenough = MsgUtils.color(getValue("vault_noenough","&f你的游戏币不足 &e%value%"));
        this.member_max = MsgUtils.color(getValue("member_max","&f公会人数已满，无法再招募更多成员！"));
        this.isowner = MsgUtils.color(getValue("isowner","&f该玩家是会长！"));

        this.help_player = MsgUtils.color(getValue("help_player",new ArrayList<>()));
        this.help_admin = MsgUtils.color(getValue("help_admin",new ArrayList<>()));

        this.notmath = MsgUtils.color(getValue("notmath","&f请输入正确的数字！"));
        this.notguild = MsgUtils.color(getValue("notguild","该公会不存在！"));
        this.notplayer = MsgUtils.color(getValue("notplayer","&f该玩家不存在！"));

        this.uset_recive_points = MsgUtils.color(getValue("user.recive_points","&f你的公会贡献点增加了 &a%value%"));
        this.uset_decrease_points = MsgUtils.color(getValue("user.decrease_points","&f你的公会贡献点减少了 &a%value%"));
        this.uset_set_points = MsgUtils.color(getValue("user.set_points","&f你的公会贡献点被设置为 &a%value%"));
        this.user_set_position = MsgUtils.color(getValue("user.set_position","&f你的公会职位变化为 &a%value%"));

        this.admin_give_points = MsgUtils.color(getValue("admin.give_points","&f玩家 &e%target% &f的公会贡献点增加了 &a%value%"));
        this.admin_take_points = MsgUtils.color(getValue("admin.take_points","&f玩家 &e%target% &f的公会贡献点减少了 &a%value%"));
        this.admin_set_points = MsgUtils.color(getValue("admin.set_points","&f玩家 &e%target% &f的公会贡献被设置为 &a%value%"));
        this.admin_target_nothasguild = MsgUtils.color(getValue("admin.target_nothasguild","&f该玩家不在公会内."));
        this.admin_set_position_null = MsgUtils.color(getValue("admin.set_position_null","&f该职位不存在！"));
        this.admin_set_position = MsgUtils.color(getValue("admin.set_position","&f玩家 &e%target% &f的职位被设置为 &a%value%"));
        this.admin_set_position_cant = MsgUtils.color(getValue("admin.set_position_cant","&f该玩家是公会会长，无法变更职位！"));
        this.admin_set_position_cant_owner = MsgUtils.color(getValue("admin.set_position_cant_owner","&f会长这个职位无法被任命"));
        this.admin_set_position_cant_max = MsgUtils.color(getValue("admin.set_position_cant_max","&f该公会的该职位人数达到上限.."));
        this.admin_give_money = MsgUtils.color(getValue("admin.give_money","&f公会 &e%target% &f的资金增加了 &a%value%"));
        this.admin_take_money = MsgUtils.color(getValue("admin.take_money","&f公会 &e%target% &f的资金减少了 &a%value%"));
        this.admin_set_money = MsgUtils.color(getValue("admin.set_money","&f公会 &e%target% &f的资金被设置为 &a%value%"));
        this.admin_level_set = MsgUtils.color(getValue("admin.level_set","&f你将公会 %target% &f的等级设置为 &a%value% &f级"));
        this.admin_level_add = MsgUtils.color(getValue("admin.level_add","&f你将公会 %target% &f的等级提升了 &a%value% &f级"));
        this.admin_level_take= MsgUtils.color(getValue("admin.level_take","&f你将公会 %target% &f的等级减少了 &a%value% &f级"));
        this.admin_exp_add = MsgUtils.color(getValue("admin.exp_add","&f你将公会 &e%target% &f的经验提升了 &e%value%"));
        this.admin_exp_take = MsgUtils.color(getValue("admin.exp_take","&f你将公会 &e%target% &f的经验减少了 &e%value%"));
        this.admin_exp_set = MsgUtils.color(getValue("admin.exp_set","&f你将公会 &e%target% &f的经验设置为 &e%value%"));
        this.admin_treelevel_set = MsgUtils.color(getValue("admin.treelevel_set","&f你将公会 %target% &f的公会神树等级设置为 &a%value% &f级"));
        this.admin_treelevel_add = MsgUtils.color(getValue("admin.treelevel_add","&f你将公会 %target% &f的公会神树等级提升了 &a%value% &f级"));
        this.admin_treelevel_take= MsgUtils.color(getValue("admin.treelevel_take","&f你将公会 %target% &f的公会神树等级减少了 &a%value% &f级"));
        this.admin_treeexp_add = MsgUtils.color(getValue("admin.treeexp_add","&f你将公会 &e%target% &f的公会神树成长值提升了 &e%value%"));
        this.admin_treeexp_take = MsgUtils.color(getValue("admin.treeexp_take","&f你将公会 &e%target% &f的公会神树成长值减少了 &e%value%"));
        this.admin_treeexp_set = MsgUtils.color(getValue("admin.treeexp_set","&f你将公会 &e%target% &f的公会神树成长值设置为 &e%value%"));
        this.admin_bufflevel_add = MsgUtils.color(getValue("admin.bufflevel_add","&f你将公会 &e%target% &f的buff %buff% &f等级提升了 &e%value% &f级！"));
        this.admin_bufflevel_take = MsgUtils.color(getValue("admin.bufflevel_take","&f你将公会 &e%target% &f的buff %buff% &f等级降低了 &e%value% &f级！"));
        this.admin_bufflevel_set = MsgUtils.color(getValue("admin.bufflevel_set","&f你将公会 &e%target% &f的buff %buff% &f等级设置为 &e%value% &f级！"));
        this.admin_buff_null = MsgUtils.color(getValue("admin.buff_null","&f该buff不存在！"));
        this.admin_activity_add = MsgUtils.color(getValue("admin.activity_add","&f你将公会 &e%target% &f的活跃度提升了 &e%value%"));
        this.admin_activity_take = MsgUtils.color(getValue("admin.activity_take","&f你将公会 &e%target% &f的活跃度减少了 &e%value%"));
        this.admin_activity_set = MsgUtils.color(getValue("admin.activity_set","&f你将公会 &e%target% &f的活跃度设置为 &e%value%"));
        this.admin_maxmembers_add = MsgUtils.color(getValue("admin.maxmembers_add","&fYou have increased the maximum number of members in the guild &e%target% &fby &e%value%"));
        this.admin_maxmembers_set = MsgUtils.color(getValue("admin.maxmembers_set","&fYou have set the maximum number of members in the guild &e%target% &fby &e%value%"));
        this.admin_teamshop_refresh = MsgUtils.color(getValue("admin.teamshop-refresh","&fYou have refresh the item of teamshop for guild &e%target%"));
        this.admin_add_icon = MsgUtils.color(getValue("admin.add-icon","&fYou have unlock the icon &e%value% &ffor guild &e%target%"));
        this.admin_remove_icon = MsgUtils.color(getValue("admin.remove-icon","&fYou have remove the icon &e%value% &ffor guild &e%target%"));
        this.admin_add_member = MsgUtils.color(getValue("admin.member_add","&fYou added player &e%target% &fto guild &e%value%"));
        this.admin_position = MsgUtils.color(getValue("admin.position","&fYou changed player &e%target% &fposition to &e%value%"));

        this.default_null = MsgUtils.color(getValue("default.null","无"));
        this.default_guild = MsgUtils.color(getValue("default.guild","无公会"));
        this.default_position = MsgUtils.color(getValue("default.position","无职位"));
        this.default_intro = MsgUtils.color(getValue("default.intro","这个公会的会长很懒,还没有留下任何介绍"));
        this.default_notice = MsgUtils.color(getValue("default.notice","这个公会的会长很懒,还没有留下任何公告"));

        this.create_cooldown = MsgUtils.color(getValue("create.cooldown","&f你还需等待 &c%value%s &f后才能加入或者创建新的公会"));
        this.create_length = MsgUtils.color(getValue("create.length","&f该公会名称过长！"));
        this.create_exists = MsgUtils.color(getValue("create.exists","&f该公会名称已经存在了！"));
        this.create_message = MsgUtils.color(getValue("create.message","&f你成功创建了公会 &e%value% &f赶快邀请玩家加入你的公会吧！"));
        this.create_broad = MsgUtils.color(getValue("create.broad",new ArrayList<>()));

        this.reuirement_notenough_vault = MsgUtils.color(getValue("requirements.notenough_vault","&f你的游戏币不足 &e%value%"));
        this.reuirement_notenough_playerpoints = MsgUtils.color(getValue("requirements.notenough_playerpoints","&f你的点券不足 &e%value%"));
        this.requirement_notenough_item = MsgUtils.color(getValue("requirements.notenough_item","&f你的 %item% &f不足 &e%value%个"));
        this.requirement_notenough_activity = MsgUtils.color(getValue("requirements.notenough_activity","&fYour activity points did not reach &e%value% &fthis week"));
        this.requirement_notenough_total_activity = MsgUtils.color(getValue("requirements.notenough_total-activity","&fYour history activity points did not reach &e%value%"));

        this.application_send = MsgUtils.color(getValue("application.send","&f你向 %value% &f发送了入会申请,请等待会长审核."));
        this.application_recive = MsgUtils.color(getValue("application.recive","&f玩家 &e%player% &f想要加入公会,请及时处理入会申请.."));
        this.application_already = MsgUtils.color(getValue("application.already","&f你已经向该公会发送过申请了,请等待审核."));
        this.application_already_in_guild = MsgUtils.color(getValue("application.already_in_guild","&f该玩家已经加入了其他的公会了.."));
        this.application_wait = MsgUtils.color(getValue("application.wait","&f你目前有 &e%value% &f个入会申请待处理.."));
        this.application_pass = MsgUtils.color(getValue("application.pass","&f你通过了玩家 &e%value% &f的入会申请."));
        this.application_join = MsgUtils.color(getValue("application.join","&f你加入了公会 %value%"));
        this.application_join_broad = MsgUtils.color(getValue("application.join_broad","&f新的成员 &e%value% &f加入了我们的公会！"));
        this.application_deny = MsgUtils.color(getValue("application.deny","&c你拒绝了 &d%value% &c的入会申请"));
        this.application_deny_target = MsgUtils.color(getValue("application.deny_target","&c你向公会 %value% &c发送的入会申请被拒绝..."));

        this.members_kick = MsgUtils.color(getValue("members.kick","&f你将 &e%value% &f踢出了公会."));
        this.members_kick_broad = MsgUtils.color(getValue("members.kick_broad","&f公会成员 &e%value% &f被踢出了公会."));
        this.members_bekick = MsgUtils.color(getValue("members.bekick","&f你被 &e%value% &f的会长踢出了公会..."));

        this.stores_unlock = MsgUtils.color(getValue("stores.unlock","&f你将公会 &E%value%号仓库 &f解锁了！"));
        this.stores_unlock_broad = MsgUtils.color(getValue("stores.unlock_broad","&f公会解锁了 &e%value%号仓库！"));
        this.stores_has_used = MsgUtils.color(getValue("stores.has_used","&f该仓库有成员正在使用.."));
        this.stores_add_white = MsgUtils.color(getValue("stores.add_white","&f你成功对 &e%target% &f开放使用 &e%value%号仓库"));
        this.stores_add_white_already = MsgUtils.color(getValue("stores.add_white_already","&f该成员已经在该号仓库的白名单内"));
        this.stores_remove_white = MsgUtils.color(getValue("stores.remove_white","&f你已关闭 &e%target% &f的 &3%value%号仓库 &f使用权"));
        this.stores_remove_white_null = MsgUtils.color(getValue("stores.remove_white_null","&f该仓库的白名单内没有该成员"));
        this.stores_add_white_title = MsgUtils.color(getValue("stores.add_white_title","&f请发送玩家名称到聊天栏, 输入 'cancel' 即可取消"));
        this.stores_remove_white_title = MsgUtils.color(getValue("stores.remove_white_title","&f请发送移除该仓库白名单内的成员名字, 输入 'cancel' 即可取消"));
        this.stores_cant_use = MsgUtils.color(getValue("stores.cant_use","&f该仓库被设置了权限，你不在该仓库的信任名单内"));
        this.stores_cant_input = MsgUtils.color(getValue("stores.cant_put","&cThis item cannot be placed in the guild store！"));

        this.redpacket_min_amount = MsgUtils.color(getValue("redpacket.min_amount","&f最少红包份数为&e 2个"));
        this.redpacket_min_total = MsgUtils.color(getValue("redpacket.min_total","&f最低红包金额为 &e100.0"));
        this.redpacket_create = MsgUtils.color(getValue("redpacket.create","&f你发放了一个公会红包，总金额 &e%total% &f共 &e%amount% &f份."));
        this.redpacket_create_broad = MsgUtils.color(getValue("redpacket.create_broad","&f公会成员 &e%target% &f发放了总金额为 &e%total% &f的红包，共 &e%amount% &f份！赶快抢吧！"));
        this.redpacket_garb_no = MsgUtils.color(getValue("redpacket.garb_no","&f你来迟了，该红包已经被瓜分完了..."));
        this.redpacket_garb_already = MsgUtils.color(getValue("redpacket.garb_already","&f你已经领取过该红包了.."));
        this.redpacket_garb = MsgUtils.color(getValue("redpacket.garb","&f公会成员 &e%target% &f领取了 &e%value% &f发放的红包，获得了 &e%money%游戏币"));
        this.redpacket_garb_finally = MsgUtils.color(getValue("redpacket.garb_finally","&f公会成员 &e%target% &f发放的红包已被领取完毕,运气王是 &e%luck% &f金额为 &e%value%"));
        this.redpacket_create_amount_max = MsgUtils.color(getValue("redpacket.create_amount_max","&f红包数量不能超过公会成员的人数"));

        this.level_levelup = MsgUtils.color(getValue("level.levelup","'&f公会等级提升！目前等级为 &e%value%"));
        this.level_levelup_broad = MsgUtils.color(getValue("level.levelup_broad", Arrays.asList(" ","&7[&6公会&7] &f公会 &e%target% &f的等级提升为 &e%value%！"," ")));
        this.level_expadd = MsgUtils.color(getValue("level.expadd","&f公会经验增加了 &e%value%"));

        this.tree_levelup = MsgUtils.color(getValue("tree.levelup","&f公会神树等级提示！当前等级为 &e%value%"));
        this.tree_levelup_byplayer = MsgUtils.color(getValue("tree.levelup_byplayer","&f你成功提升了公会神树的等级！"));
        this.tree_level_large = MsgUtils.color(getValue("tree.level_large","&f神树等级无法超过公会等级！"));
        this.tree_levelup_cant = MsgUtils.color(getValue("tree.levelup_cant","公会神树成长值不足！无法升级..."));
        this.tree_level_max = MsgUtils.color(getValue("tree.level_max","&f神树等级已达到上限."));
        this.tree_expadd_byplayer = MsgUtils.color(getValue("tree.expadd_byplayer","&f公会神树成长值提升了 &a%value%"));
        this.tree_wish_refresh = MsgUtils.color(getValue("tree.wish_refresh","&bThe opportunity to make a wish on the tree has been refreshed today. Hurry up and go to the guild tree to make your wish！"));
        this.tree_wish = MsgUtils.color(getValue("tree.wish","&a许愿成功！获得神树的馈赠！"));
        this.tree_wish_broad = MsgUtils.color(getValue("tree.wish_broad","&f公会成员 &e%target% &f今日完成神树许愿并获得了神树的馈赠！"));
        this.tree_wish_already = MsgUtils.color(getValue("tree.wish_already","&f你今日已经许愿过了.."));
        this.tree_water_refresh = MsgUtils.color(getValue("tree.water_refresh","&bThe number of times the tree pot has been refreshed today. Hurry up and go to the guild tree to water it！"));
        this.tree_water = MsgUtils.color(getValue("tree.water","&f你成功使用 %value% &f为神树浇灌！"));
        this.tree_water_broad = MsgUtils.color(getValue("tree.water_broad","&f公会成员使用 %value% &f为神树进行了浇灌."));
        this.tree_water_limit = MsgUtils.color(getValue("tree.water_limit","&f你今日不能再使用该水壶了！"));

        this.icon_unlock = MsgUtils.color(getValue("icon.unlock","&f你为公会解锁了公会图标 %value%"));
        this.icon_locked = MsgUtils.color(getValue("icon.locked","&f该图标还未解锁"));
        this.icon_put = MsgUtils.color(getValue("icon.put","&f你将公会图标更改为 %value%"));
        this.icon_not_exist = MsgUtils.color(getValue("icon.not-exist","&fThe icon is not exist."));

        this.shop_buy = MsgUtils.color(getValue("shop.buy","&f你购买了 %value%"));
        this.shop_limit = MsgUtils.color(getValue("shop.limit","&f你不能再购买该商品了..."));
        this.shop_refresh_day = MsgUtils.color(getValue("shop.refresh_day","&fThe daily purchase limit for guild stores has been refreshed.."));
        this.shop_refresh_week = MsgUtils.color(getValue("shop.refresh_week","&fThe weekly purchase limit for guild stores has been refreshed.."));
        this.shop_refresh_month = MsgUtils.color(getValue("shop.refresh_month","&fThe monthly purchase limit for guild stores has been refreshed.."));


        this.buff_levelup = MsgUtils.color(getValue("buff.levelup","&d公会buff %target% &d等级提升,目前为 &e%value% &d级."));
        this.buff_max = MsgUtils.color(getValue("buff.max","&c该Buff的等级已经达到上限."));
        this.buff_cant = MsgUtils.color(getValue("buff.cant","&cBuff的等级不能超过公会等级."));

        this.pvp_cant = MsgUtils.color(getValue("pvp.cant","&f你已开启公会保护模式,无法攻击同一个公会的玩家！"));
        this.pvp_cant_target = MsgUtils.color(getValue("pvp.cant_target","&f对方已开启公会保护模式，无法对其造成伤害！"));
        this.pvp_enable = MsgUtils.color(getValue("pvp.enable","&c你已关闭公会保护模式，现在将会对同一个公会的玩家造成伤害."));
        this.pvp_disable = MsgUtils.color(getValue("pvp.disable","&f你已开启公会保护模式,无法攻击同一个公会的玩家！"));

        this.quit_message = MsgUtils.color(getValue("quit.message","&4你退出了公会 %value%"));
        this.quit_broad = MsgUtils.color(getValue("quit.broad","&c公会成员 %position%%value% &c退出了公会...."));
        this.quit_owner = MsgUtils.color(getValue("quit.owner","&f你的公会会长，你只能&e转让、解散&f公会."));


        this.delete_confirm = MsgUtils.color(getValue("delete.confirm","&4你确定要解散公会吗？请使用指令 &e/guild delete confirm &4以解散公会."));
        this.delete_message = MsgUtils.color(getValue("delete.message","&7你解散了 %value%"));
        this.delete_broad_members = MsgUtils.color(getValue("delete.broad_members","&c你所在的公会已被解散..."));
        this.delete_broad = MsgUtils.color(getValue("delete.broad",Arrays.asList("","&7[&6公会&7] &C公会 &e%value% &C已被解散了..","")));

        this.give_message = MsgUtils.color(getValue("give.message","&c你将公会 %value% &c转让给了 &e%target%"));
        this.give_message_target = MsgUtils.color(getValue("give.message_target","&6原公会会长已将公会 %value% &6转让给你了."));
        this.give_broad = MsgUtils.color(getValue("give.broad","&e新的会长 &a%value% &e上任，原会长已卸甲归田..."));

        this.activity_gain = MsgUtils.color(getValue("activity.gain","&a公会活跃度提升了 &e%value%"));
        this.activity_join_tip = MsgUtils.color(getValue("activity.join_tip","&a你有未领取的公会活跃度奖励."));
        this.activity_claim = MsgUtils.color(getValue("activity.claim","&e你领取了 %value%"));
        this.activity_cant_claim = MsgUtils.color(getValue("activity.cant_claim","&c当前公会活跃度不足 &e%value%"));
        this.activity_already_claimed = MsgUtils.color(getValue("activity.already_claimed","&c你已经领取过该活跃度奖励了."));

        this.positions_add_write = MsgUtils.color(getValue("positions.add_write","&f请输入成员名称以任命 %position% &f输入 &c'cancel' &f取消"));
        this.positions_remove_write = MsgUtils.color(getValue("positions.remove_write","&f请输入成员名称以取消任命 %position% &f输入 &c'cancel' &f取消"));
        this.positions_max = MsgUtils.color(getValue("positions.max","&f该职位当前在位人数已经达到上限.."));
        this.positions_message = MsgUtils.color(getValue("positions.message","&e你任命成员 &a%target% &e为 %value%"));
        this.positions_message_target = MsgUtils.color(getValue("positions.message_target","&a你被公会会长任命为 %value%"));
        this.positions_message_cancel= MsgUtils.color(getValue("positions.message_cancel","&c你将 &f%value% &c的职位取消了."));
        this.positions_message_cancel_target= MsgUtils.color(getValue("positions.message_cancel_target","&c你的职位已被取消."));

        this.money_message= MsgUtils.color(getValue("money.message","&a你向公会捐赠了 &e%value%"));
        this.money_message_broad= MsgUtils.color(getValue("money.message_broad","&b公会成员 &f%target% &b向公会捐赠了 &f%value% &b资金."));

        this.home_home_null= MsgUtils.color(getValue("home.home_null","&c公会还未设置驻地."));
        this.home_set= MsgUtils.color(getValue("home.set","&e你将公会驻地更改到当前位置."));
        this.home_set_broad= MsgUtils.color(getValue("home.set_broad","&c公会驻地已被更改."));
        this.home_wait= MsgUtils.color(getValue("home.wait","&e请等待 &a%value%s &e后返回公会驻地,在此期间请不要移动.."));
        this.home_teleport= MsgUtils.color(getValue("home.teleport","&a已传送至公会驻地."));
        this.home_cancel= MsgUtils.color(getValue("home.cancel","&c你取消了本次传送."));
        this.home_cant_world= MsgUtils.color(getValue("home.cant_world","&c该世界禁止设置驻地"));
        this.home_cant_server= MsgUtils.color(getValue("home.cant_server","&c该大区禁止设置驻地"));

        this.intro_add= MsgUtils.color(getValue("intro.add","&a你添加了一条新的公会介绍：&f%value%"));
        this.intro_remove= MsgUtils.color(getValue("intro.remove","&c你移除了最新后一条公会介绍：&f%value%"));
        this.notice_add= MsgUtils.color(getValue("notice.add","&a你添加了一条新的公会公告：&f%value%"));
        this.notice_remove= MsgUtils.color(getValue("notice.remove","&c你移除了最新后一条公会公告：&f%value%"));

        this.chat_enable = MsgUtils.color(getValue("chat.enable","&a你已开启公会聊天,接下来的消息仅会被公会成员所看见."));
        this.chat_disable = MsgUtils.color(getValue("chat.disable","&c你关闭了公会聊天,接下的消息将被全体玩家所看见."));

        this.reset_activity = MsgUtils.color(getValue("reset.activity","&cYou have reset the guild activity data for &f%guild%"));
        this.reset_shop = MsgUtils.color(getValue("reset.shop","&cYou have reset the player %type% shop date for &e%player%"));
        this.reset_wish = MsgUtils.color(getValue("reset.wish","&cYou have reset the guild tree wish data for &e%player%"));
        this.reset_pot = MsgUtils.color(getValue("reset.pot","&cYou have reset the guild tree water data for &e%player% &c, id &e%pot%"));
        this.reset_guild_teamshop = MsgUtils.color(getValue("reset.guild-teamshop","&cYou have reset the guild teamshop data for the members of &f%guild%"));
        this.reset_user_teamshop = MsgUtils.color(getValue("reset.user-teamshop","&cYou have reset the guild team shop data to &e%amount% &cfor &e%player%"));
        this.reset_user_cooldown = MsgUtils.color(getValue("reset.cooldown","&cYou have reset the cooldown time for joining &e%player% &cto &e%second% &cseconds"));

        this.bargain_already = MsgUtils.color(getValue("bargain.already","&cYou have already discounted the price."));
        this.bargain_success = MsgUtils.color(getValue("bargain.success","&aYou have successfully negotiated a price of &e%bargain%, &aand the current group purchase price is &e%current%"));
        this.bargain_bargain_broad = MsgUtils.color(getValue("bargain.bargain-broad","&6Guild members &f%player% &6slashed the price of group buying products, resulting in a decrease of &a%bargain% &6.Currently, the price of group buying products is &a%current%"));
        this.bargain_buy = MsgUtils.color(getValue("bargain.buy","&aYou have successfully purchased the guild group buying gift pack %display%"));
        this.bargain_buy_broad = MsgUtils.color(getValue("bargain.buy-broad","&6Guild members &f%player% &6purchased the guild group buying gift pack %display% &6at a price of &a%price%&6"));
        this.bargain_buy_limit = MsgUtils.color(getValue("bargain.buy-limit","&cYou have already purchased this gift pack &f%limit% &ctimes today and cannot make any further purchases！"));
    }
}
