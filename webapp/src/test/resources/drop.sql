alter table Address 
    drop constraint FK_b9wby4b3jwmvb3t6u4m0jlygg if exists;

alter table Address 
    drop constraint FK_33hg5keygw64femiy5lgd4y9t if exists;

alter table Binary_ 
    drop constraint FK_t8vmhvwngg2vtjls1gyf3vjyv if exists;

alter table BlogEntry 
    drop constraint FK_hhpchf7bv5ratxwq3jqsie5fk if exists;

alter table BlogEntry 
    drop constraint FK_pv1oa95stblj2x19q851906vs if exists;

alter table BlogEntry 
    drop constraint FK_4jxfnu2hx10gjhyr9teqpgh2s if exists;

alter table BlogEntryTag 
    drop constraint FK_ncofal77nq7rmrlprnrfb2cmb if exists;

alter table BlogEntryTag 
    drop constraint FK_qno46w1xh2wt3h0nlagr35e62 if exists;

alter table BookPublication 
    drop constraint FK_ju2xorsfflj7raab3cv079l5x if exists;

alter table BookPublication 
    drop constraint FK_3bg7owacmg2nva6bl6btfwmwi if exists;

alter table BookPublication 
    drop constraint FK_truqclteayq1fpva8iubn43t5 if exists;

alter table CoOpsSession 
    drop constraint FK_wob1x94bufe9nqq8bclvnhy7 if exists;

alter table CoOpsSession 
    drop constraint FK_gxfr8cvpe9c53dck2nt6ujrqs if exists;

alter table Document 
    drop constraint FK_1jry86o40p7fcuo1fi564tgcj if exists;

alter table DocumentRevision 
    drop constraint FK_5c5yqhl4icfm3ts9x0dqwbreg if exists;

alter table DocumentRevision 
    drop constraint FK_bb2yvl5c9q98ffnxs2tyfly8l if exists;

alter table DropboxFile 
    drop constraint FK_763v1y6sj5clsl1yck5mq2l1m if exists;

alter table DropboxFolder 
    drop constraint FK_l35xp1hpw367749yi79n66fnf if exists;

alter table DropboxRootFolder 
    drop constraint FK_9g0c9ihbvbu0cnjcdllripp4n if exists;

alter table File 
    drop constraint FK_17vh64nwb61tixrxnviulrva2 if exists;

alter table Folder 
    drop constraint FK_idx3wiwdm8yp2qkkddi726n8o if exists;

alter table Forum 
    drop constraint FK_5m8qqcnsb5gsif8taa58khwwd if exists;

alter table ForumMessage 
    drop constraint FK_12mkuto37lpbyavjp36il7fk6 if exists;

alter table ForumPost 
    drop constraint FK_oin05xpq2f3lvp22jpu4mtppu if exists;

alter table ForumPost 
    drop constraint FK_k4o4gqltnknpvkn33fhbajf07 if exists;

alter table ForumTopic 
    drop constraint FK_ft3gjajs954n8do2y1vgd36m1 if exists;

alter table ForumTopic 
    drop constraint FK_pqua2p3mkee2bacxf19c73v3o if exists;

alter table ForumTopicWatcher 
    drop constraint FK_3y506ftpsm1ynn859skf0x8c3 if exists;

alter table ForumTopicWatcher 
    drop constraint FK_n0h7phcrsoyduqwl1cndho5uy if exists;

alter table FriendConfirmKey 
    drop constraint FK_wfblwxkfts5g3fg7ee0nfd2u if exists;

alter table FriendConfirmKey 
    drop constraint FK_9561x0eub9681y890bbh8oa2m if exists;

alter table GoogleDocument 
    drop constraint FK_rohgbl8frsua16jnumxo9y7gq if exists;

alter table IllusionFolder 
    drop constraint FK_qieidhiuhk09j5m6l6pqkt0a5 if exists;

alter table IllusionGroup 
    drop constraint FK_1ccfwpe2ygxulx35ykxspqsmq if exists;

alter table IllusionGroupDocument 
    drop constraint FK_i1ctgxnegc2wvdcdfwm9oruo6 if exists;

alter table IllusionGroupFolder 
    drop constraint FK_qye4g9cukldbot5k5u0w6k8en if exists;

alter table IllusionGroupMember 
    drop constraint FK_4ney1jxmm0a7qsavn23n11pbc if exists;

alter table IllusionGroupMember 
    drop constraint FK_9ok316p3y0mvfqf167cudt5xu if exists;

alter table IllusionGroupMemberImage 
    drop constraint FK_lk3e3jpg8ky1bl1kxb1ce3oat if exists;

alter table IllusionGroupMemberSetting 
    drop constraint FK_af78vixxjq03nocl2xt85ym3w if exists;

alter table IllusionGroupSetting 
    drop constraint FK_hdqeshicq23nolw9j160rpl0a if exists;

alter table Image 
    drop constraint FK_9xcia6idnwqdi9xx8ytea40h3 if exists;

alter table ImageRevision 
    drop constraint FK_ae927momh8i44pw15h7c8v883 if exists;

alter table ImageRevision 
    drop constraint FK_opfsl8bg6s8k9h879ec1x5eug if exists;

alter table InternalAuth 
    drop constraint FK_4ev60hulclv68yy2b7i7no069 if exists;

alter table Material 
    drop constraint FK_fx7vtonuv6g0a9kdbev72aj71 if exists;

alter table Material 
    drop constraint FK_b3woqnul7laianx9030b1roh0 if exists;

alter table Material 
    drop constraint FK_gps5qt45nwfqe1psjkov77e0g if exists;

alter table Material 
    drop constraint FK_r9eehdvglf4xvj55m3o8ow2s6 if exists;

alter table MaterialRevisionSetting 
    drop constraint FK_ct6frqhrvcsxdx8c4kitv68wd if exists;

alter table MaterialRevisionSetting 
    drop constraint FK_gurfakq9wbav6msh4f20tg3y7 if exists;

alter table MaterialRevisionTag 
    drop constraint FK_rjpc4n4ab456l6fkffs711b7f if exists;

alter table MaterialRevisionTag 
    drop constraint FK_8khhkh39wfk7wy83s2b76kht if exists;

alter table MaterialSetting 
    drop constraint FK_kx5dhocydap0gugdij1yoayig if exists;

alter table MaterialSetting 
    drop constraint FK_skdihk14gde1eefchngsftv7w if exists;

alter table MaterialTag 
    drop constraint FK_3js3omusv545pec4rofbsjlfj if exists;

alter table MaterialTag 
    drop constraint FK_1y9txx76jb95g1atbe17pvr33 if exists;

alter table MaterialThumbnail 
    drop constraint FK_9s2fwcpsnmo374012ia4l7xr9 if exists;

alter table MaterialView 
    drop constraint FK_c5t1qubcegltq6le7yg7rrgon if exists;

alter table MaterialView 
    drop constraint FK_kc05eno56sd0jdwu0pdbepoos if exists;

alter table Message 
    drop constraint FK_tbto6hemu447oixxk730el2vx if exists;

alter table MessageFolder 
    drop constraint FK_o3f1777quj13cge2kd7llwoar if exists;

alter table OrderItem 
    drop constraint FK_fkri6q4oaft141fa93qhilx9w if exists;

alter table OrderItem 
    drop constraint FK_6cxptya5vldowhtfdxs70ytw1 if exists;

alter table OrderItem 
    drop constraint FK_bexee46kvwhk7kmi5mirwvf94 if exists;

alter table Order_ 
    drop constraint FK_nuektq0t0wsfj2yos4jaga5yq if exists;

alter table Order_ 
    drop constraint FK_4jexecwfjhyb7fmjbf1cpwfbx if exists;

alter table PasswordResetKey 
    drop constraint FK_ig2l7ybgill2j63l9rsdtsvhd if exists;

alter table Pdf 
    drop constraint FK_2l0kstgj1ayjmh2968qgvmw6q if exists;

alter table PermaLink 
    drop constraint FK_5mh9gi6vjsvnhyny4f8489hdh if exists;

alter table Publication 
    drop constraint FK_4spcv9w9c2hsbhdcktsi5ahb2 if exists;

alter table Publication 
    drop constraint FK_45r8ijdx482x9l31s0ptvdurs if exists;

alter table Publication 
    drop constraint FK_e2nks3e781go58t5fq804g20b if exists;

alter table Publication 
    drop constraint FK_9iwnkkslclyi784x9nfto9915 if exists;

alter table Publication 
    drop constraint FK_lovvwguqn722f3upi5kehro4r if exists;

alter table PublicationAuthor 
    drop constraint FK_mnr5qymj4y7kpam7gwn20gbno if exists;

alter table PublicationAuthor 
    drop constraint FK_82528is9e0hklum9r8ol28ibw if exists;

alter table PublicationImage 
    drop constraint FK_kp5cv5yvta2aurqe9nv6gxyir if exists;

alter table PublicationImage 
    drop constraint FK_8apjtolm7i5qnnw9ddx916jps if exists;

alter table PublicationImage 
    drop constraint FK_6jn47ypu7k2yskfs86olxwy1b if exists;

alter table PublicationTag 
    drop constraint FK_boonyg7478u8t1ypeipj1pmkl if exists;

alter table PublicationTag 
    drop constraint FK_toxebsnjvyi3o0ckg8fcfm8d0 if exists;

alter table RecipientMessage 
    drop constraint FK_jpwylts3n7aaj8glqxxfjk9f8 if exists;

alter table RecipientMessage 
    drop constraint FK_a5jq61n718lkefe477rbk1981 if exists;

alter table RecipientMessage 
    drop constraint FK_29stadpyo7wj3605qp9xrwimy if exists;

alter table ShoppingCart 
    drop constraint FK_226ui9my3sceiilf5pmd35g7l if exists;

alter table ShoppingCart 
    drop constraint FK_4oe973j8s9242kguphe3so2rx if exists;

alter table ShoppingCartItem 
    drop constraint FK_am5qynab11y7xgjbgst5ulfff if exists;

alter table ShoppingCartItem 
    drop constraint FK_1hr90hiipfmghjy3ur6x2qwc9 if exists;

alter table StarredMaterial 
    drop constraint FK_etvdnq9ekq5l1g5xfyxthg3tl if exists;

alter table StarredMaterial 
    drop constraint FK_wbtdleyqvpwjlnc8rjmmf6s8 if exists;

alter table UserChatCredentials 
    drop constraint FK_h3ipddn5k0nejikrmpayobxcx if exists;

alter table UserContactField 
    drop constraint FK_ftmlh7t6ki89bgge2ebmdf2li if exists;

alter table UserEmail 
    drop constraint FK_eyclispg2p45hk6ryxn0rurq4 if exists;

alter table UserFriend 
    drop constraint FK_1moblwb5r358wnd77acxga4mn if exists;

alter table UserFriend 
    drop constraint FK_a01cuibcreifm5la30a329hn7 if exists;

alter table UserIdentifier 
    drop constraint FK_lnyj92dpxse5ma8bq2n8cs4cg if exists;

alter table UserImage 
    drop constraint FK_ek8itxfnsp5le03hxbta0749w if exists;

alter table UserMaterialRole 
    drop constraint FK_nq4i53ppmmjbrhd30vgfinwpp if exists;

alter table UserMaterialRole 
    drop constraint FK_lsvoxxuju09swlpvn1qcoxlik if exists;

alter table UserSetting 
    drop constraint FK_qt91lfx3ktlndkjknu1qhykyp if exists;

alter table UserSetting 
    drop constraint FK_opl68ghfxbq51vsl8d2c6p1v5 if exists;

alter table UserToken 
    drop constraint FK_id71ppyyw0tkkj8c97n935lk4 if exists;

alter table UserVerificationKey 
    drop constraint FK_icq5jrfca3mudiw4kpe9s0q4d if exists;

alter table VectorImage 
    drop constraint FK_o0mft3m1vbns8qsy7mv72n8e6 if exists;

alter table VectorImageRevision 
    drop constraint FK_8aghc2j34pfkekkyuxsk6gf7y if exists;

alter table VectorImageRevision 
    drop constraint FK_qxq9qp6lutv7aml83qypblfaq if exists;

drop table Address if exists;

drop table Binary_ if exists;

drop table BlogCategory if exists;

drop table BlogEntry if exists;

drop table BlogEntryTag if exists;

drop table BlogTag if exists;

drop table BookPublication if exists;

drop table CoOpsSession if exists;

drop table Country if exists;

drop table Document if exists;

drop table DocumentRevision if exists;

drop table DropboxFile if exists;

drop table DropboxFolder if exists;

drop table DropboxRootFolder if exists;

drop table File if exists;

drop table Folder if exists;

drop table Forum if exists;

drop table ForumCategory if exists;

drop table ForumMessage if exists;

drop table ForumPost if exists;

drop table ForumTopic if exists;

drop table ForumTopicWatcher if exists;

drop table FriendConfirmKey if exists;

drop table GameLibraryTag if exists;

drop table GoogleDocument if exists;

drop table IllusionFolder if exists;

drop table IllusionGroup if exists;

drop table IllusionGroupDocument if exists;

drop table IllusionGroupFolder if exists;

drop table IllusionGroupMember if exists;

drop table IllusionGroupMemberImage if exists;

drop table IllusionGroupMemberSetting if exists;

drop table IllusionGroupSetting if exists;

drop table Image if exists;

drop table ImageRevision if exists;

drop table InternalAuth if exists;

drop table Language if exists;

drop table Material if exists;

drop table MaterialRevision if exists;

drop table MaterialRevisionSetting if exists;

drop table MaterialRevisionTag if exists;

drop table MaterialSetting if exists;

drop table MaterialSettingKey if exists;

drop table MaterialTag if exists;

drop table MaterialThumbnail if exists;

drop table MaterialView if exists;

drop table Message if exists;

drop table MessageFolder if exists;

drop table OrderItem if exists;

drop table Order_ if exists;

drop table PasswordResetKey if exists;

drop table Pdf if exists;

drop table PermaLink if exists;

drop table Publication if exists;

drop table PublicationAuthor if exists;

drop table PublicationFile if exists;

drop table PublicationImage if exists;

drop table PublicationTag if exists;

drop table RecipientMessage if exists;

drop table ShoppingCart if exists;

drop table ShoppingCartItem if exists;

drop table StarredMaterial if exists;

drop table SystemSetting if exists;

drop table Tag if exists;

drop table User if exists;

drop table UserChatCredentials if exists;

drop table UserContactField if exists;

drop table UserEmail if exists;

drop table UserFriend if exists;

drop table UserIdentifier if exists;

drop table UserImage if exists;

drop table UserMaterialRole if exists;

drop table UserSetting if exists;

drop table UserSettingKey if exists;

drop table UserToken if exists;

drop table UserVerificationKey if exists;

drop table VectorImage if exists;

drop table VectorImageRevision if exists;