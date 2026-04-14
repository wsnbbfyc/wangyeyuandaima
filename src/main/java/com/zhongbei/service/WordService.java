package com.zhongbei.service;

import com.zhongbei.model.Word;
import com.zhongbei.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    
    @Autowired
    private WordRepository wordRepository;
    
    public List<Word> findAll() {
        return wordRepository.findAll();
    }
    
    public Optional<Word> findById(Long id) {
        return wordRepository.findById(id);
    }
    
    public Word save(Word word) {
        return wordRepository.save(word);
    }
    
    public void deleteById(Long id) {
        wordRepository.deleteById(id);
    }
    
    public List<Word> findUnlearnedWords() {
        return wordRepository.findByIsLearnedFalse();
    }
    
    public List<Word> findLearnedWords() {
        return wordRepository.findByIsLearnedTrue();
    }
    
    public List<Word> findWordsForReview(int limit) {
        return wordRepository.findRandomUnlearnedWords(limit);
    }
    
    public List<Word> findRandomWords(int limit) {
        return wordRepository.findRandomWords(limit);
    }
    
    public long countTotal() {
        return wordRepository.count();
    }
    
    public long countUnlearned() {
        return wordRepository.countByIsLearnedFalse();
    }
    
    public long countLearned() {
        return wordRepository.countByIsLearnedTrue();
    }
    
    public void markAsLearned(Long id) {
        Optional<Word> wordOpt = wordRepository.findById(id);
        if (wordOpt.isPresent()) {
            Word word = wordOpt.get();
            word.setIsLearned(true);
            word.setLearnCount(word.getLearnCount() + 1);
            word.setLastLearned(LocalDateTime.now());
            wordRepository.save(word);
        }
    }
    
    public void markAsWrong(Long id) {
        Optional<Word> wordOpt = wordRepository.findById(id);
        if (wordOpt.isPresent()) {
            Word word = wordOpt.get();
            word.setWrongCount(word.getWrongCount() + 1);
            word.setIsLearned(false);
            wordRepository.save(word);
        }
    }
    
    public void resetProgress() {
        List<Word> words = wordRepository.findAll();
        for (Word word : words) {
            word.setIsLearned(false);
            word.setLearnCount(0);
            word.setWrongCount(0);
        }
        wordRepository.saveAll(words);
    }
    
    public List<Word> search(String keyword) {
        return wordRepository.findByWordContaining(keyword);
    }
    
    @PostConstruct
    public void initDefaultWords() {
        if (wordRepository.count() == 0) {
            addDefaultCET4Words();
        }
    }
    
    private void addDefaultCET4Words() {
        String[][] words = {
            {"abandon", "əˈbændən", "vt. 放弃；抛弃 n. 放任", "He had to abandon his car because it was too old.", "他不得不抛弃他的旧车。", "v."},
            {"ability", "əˈbɪləti", "n. 能力；才能", "She has the ability to speak three languages.", "她有说三种语言的能力。", "n."},
            {"able", "ˈeɪbl", "adj. 能够的；有能力的", "He is able to finish the work on time.", "他能够按时完成工作。", "adj."},
            {"about", "əˈbaʊt", "prep. 关于；大约 adv. 大约", "The book is about history.", "这本书是关于历史的。", "prep."},
            {"above", "əˈbʌv", "prep. 在...上面 adj. 上面的", "The plane flew above the clouds.", "飞机在云层上方飞行。", "prep."},
            {"abroad", "əˈbrɔːd", "adv. 到国外；在国外", "She wants to study abroad.", "她想出国留学。", "adv."},
            {"absence", "ˈæbsəns", "n. 缺席；缺乏", "His absence was noticed by the teacher.", "老师注意到了他的缺席。", "n."},
            {"absolute", "ˈæbsəluːt", "adj. 绝对的；完全的", "It is an absolute necessity.", "这是绝对必要的。", "adj."},
            {"absorb", "əbˈzɔːrb", "vt. 吸收；吸引", "Plants absorb water from the soil.", "植物从土壤中吸收水分。", "v."},
            {"abstract", "ˈæbstrækt", "adj. 抽象的 n. 摘要", "The concept of time is abstract.", "时间的概念是抽象的。", "adj."},
            {"academic", "ˌækəˈdemɪk", "adj. 学术的；学院的", "He has excellent academic achievements.", "他有优秀的学业成绩。", "adj."},
            {"accept", "əkˈsept", "vt. 接受；承认", "Please accept my invitation.", "请接受我的邀请。", "v."},
            {"access", "ˈækses", "n. 进入；访问 vt. 访问", "Students have access to the library.", "学生可以进入图书馆。", "n."},
            {"accident", "ˈæksɪdənt", "n. 事故；意外", "The accident happened last night.", "事故发生在昨晚。", "n."},
            {"accompany", "əˈkʌmpəni", "vt. 陪伴；伴随", "She accompanied her friend to the hospital.", "她陪朋友去医院。", "v."},
            {"accomplish", "əˈkʌmplɪʃ", "vt. 完成；达到", "I accomplished my goal.", "我完成了我的目标。", "v."},
            {"according", "əˈkɔːrdɪŋ", "prep. 根据；按照", "According to the weather report, it will rain tomorrow.", "根据天气预报，明天会下雨。", "prep."},
            {"account", "əˈkaʊnt", "n. 账户；叙述 vi. 说明", "He opened a bank account.", "他开了个银行账户。", "n."},
            {"accurate", "ˈækjərət", "adj. 准确的；精确的", "The clock shows accurate time.", "这个时钟显示准确的时间。", "adj."},
            {"achieve", "əˈtʃiːv", "vt. 完成；达到 vi. 成功", "She achieved great success in her career.", "她在事业上取得了巨大成功。", "v."},
            {"acknowledge", "əkˈnɒlɪdʒ", "vt. 承认；确认", "I acknowledge my mistake.", "我承认我的错误。", "v."},
            {"acquire", "əˈkwaɪər", "vt. 获得；学到", "She acquired a good knowledge of English.", "她习得了扎实的英语知识。", "v."},
            {"across", "əˈkrɒs", "prep. 横过；穿过 adv. 横过", "The bridge goes across the river.", "桥横跨这条河。", "prep."},
            {"action", "ˈækʃən", "n. 行动；动作", "We need immediate action.", "我们需要立即行动。", "n."},
            {"active", "ˈæktɪv", "adj. 积极的；活跃的", "He is an active participant in the discussion.", "他是讨论的积极参与者。", "adj."},
            {"activity", "ækˈtɪvəti", "n. 活动；行动", "The club has many activities.", "这个俱乐部有很多活动。", "n."},
            {"actual", "ˈæktʃuəl", "adj. 实际的；真实的", "What are your actual feelings?", "你的真实感受是什么？", "adj."},
            {"adapt", "əˈdæpt", "vt. 使适应；改编", "We must adapt to the new environment.", "我们必须适应新环境。", "v."},
            {"add", "æd", "vt. 添加；增加", "Please add some sugar.", "请加些糖。", "v."},
            {"address", "əˈdres", "n. 地址 vt. 演说；处理", "What is your email address?", "你的电子邮件地址是什么？", "n."},
            {"adequate", "ˈædɪkwət", "adj. 足够的；适当的", "The food is adequate for everyone.", "食物足够每个人吃。", "adj."},
            {"adjust", "əˈdʒʌst", "vt. 调整；使适应", "You can adjust the seat height.", "你可以调整座椅高度。", "v."},
            {"administration", "ədˌmɪnɪˈstreɪʃən", "n. 管理；行政", "The school administration is very efficient.", "学校行政管理非常高效。", "n."},
            {"admire", "ədˈmaɪər", "vt. 钦佩；欣赏", "I admire her courage.", "我钦佩她的勇气。", "v."},
            {"admit", "ədˈmɪt", "vt. 承认；准许进入", "He admitted his mistake.", "他承认了他的错误。", "v."},
            {"adopt", "əˈdɒpt", "vt. 收养；采用", "They decided to adopt a child.", "他们决定收养一个孩子。", "v."},
            {"adult", "ˈædʌlt", "n. 成年人 adj. 成年的", "This movie is for adults only.", "这部电影仅限成年人观看。", "n."},
            {"advance", "ədˈvæns", "vi. 前进；进展 n. 进步", "Technology continues to advance.", "科技持续进步。", "v."},
            {"advantage", "ədˈvæntɪdʒ", "n. 优势；优点", "There are many advantages to living in the city.", "住在城市有很多好处。", "n."},
            {"adventure", "ədˈventʃər", "n. 冒险；奇遇", "The journey was a great adventure.", "这趟旅程是一次伟大的冒险。", "n."},
            {"advertise", "ˈædvərtaɪz", "vt. 登广告；宣传", "They advertised their products on TV.", "他们在电视上宣传他们的产品。", "v."},
            {"advice", "ədˈvaɪs", "n. 建议；劝告", "Can you give me some advice?", "你能给我一些建议吗？", "n."},
            {"advise", "ədˈvaɪz", "vt. 建议；劝告", "I advise you to start early.", "我建议你早点开始。", "v."},
            {"affair", "əˈfer", "n. 事务；事件", "The wedding was a grand affair.", "那场婚礼是一场盛大的活动。", "n."},
            {"affect", "əˈfekt", "vt. 影响；感动", "The weather affects my mood.", "天气影响我的心情。", "v."},
            {"afford", "əˈfɔːrd", "vt. 负担得起；提供", "I cannot afford a new car.", "我买不起新车。", "v."},
            {"afraid", "əˈfreɪd", "adj. 害怕的；担心的", "Don't be afraid to ask questions.", "不要害怕问问题。", "adj."},
            {"after", "ˈæftər", "prep. 在...之后 conj. 在...后", "Let's meet after school.", "我们放学后见。", "prep."},
            {"afternoon", "ˌæftərˈnuːn", "n. 下午", "I'll call you this afternoon.", "我今天下午给你打电话。", "n."},
            {"again", "əˈɡen", "adv. 再一次；又", "Please try again.", "请再试一次。", "adv."},
            {"against", "əˈɡenst", "prep. 反对；靠着", "He leaned against the wall.", "他靠在墙上。", "prep."},
            {"age", "eɪdʒ", "n. 年龄；时代 v. 变老", "What is your age?", "你多大了？", "n."},
            {"agency", "ˈeɪdʒənsi", "n. 代理；机构", "She works for a travel agency.", "她在一家旅行社工作。", "n."},
            {"agent", "ˈeɪdʒənt", "n. 代理人；代理商", "He is a sales agent.", "他是一名销售代理。", "n."},
            {"agree", "əˈɡriː", "vi. 同意；赞成", "I agree with you.", "我同意你的看法。", "v."},
            {"agreement", "əˈɡriːmənt", "n. 协议；同意", "We reached an agreement.", "我们达成了协议。", "n."},
            {"ahead", "əˈhed", "adv. 在前面；提前", "Go straight ahead.", "一直往前走。", "adv."},
            {"aid", "eɪd", "n. 援助；帮助 vt. 帮助", "They provided first aid to the victim.", "他们对受害者进行了急救。", "n."},
            {"aim", "eɪm", "n. 目标；瞄准 v. 旨在", "What is your aim in life?", "你的人生目标是什么？", "n."},
            {"air", "er", "n. 空气；天空 vt. 通风", "The air is fresh here.", "这里的空气很清新。", "n."},
            {"aircraft", "ˈerkræft", "n. 飞机；航空器", "The aircraft landed safely.", "飞机安全着陆。", "n."},
            {"airline", "ˈerlaɪn", "n. 航空公司", "Which airline do you prefer?", "你喜欢哪家航空公司？", "n."},
            {"airport", "ˈerpɔːrt", "n. 机场", "We arrived at the airport early.", "我们早早到了机场。", "n."},
            {"alarm", "əˈlɑːrm", "n. 警报；闹钟 vt. 警告", "The alarm went off at six.", "闹钟六点响了。", "n."},
            {"album", "ˈælbəm", "n. 相册；专辑", "This is a photo album.", "这是一本相册。", "n."},
            {"alcohol", "ˈælkəhɔːl", "n. 酒精；酒", "Alcohol is bad for health.", "酒精对身体有害。", "n."},
            {"alike", "əˈlaɪk", "adj. 相似的 adv. 相似地", "The two sisters look alike.", "两姐妹长得很像。", "adj."},
            {"alive", "əˈlaɪv", "adj. 活着的；活跃的", "Is she still alive?", "她还活着吗？", "adj."},
            {"allow", "əˈlaʊ", "vt. 允许；给予", "Smoking is not allowed here.", "这里不允许吸烟。", "v."},
            {"almost", "ˈɔːlmoʊst", "adv. 几乎；差不多", "It's almost time for dinner.", "快到晚饭时间了。", "adv."},
            {"alone", "əˈloʊn", "adj. 单独的 adv. 独自", "She lives alone.", "她独自生活。", "adj."},
            {"along", "əˈlɔːŋ", "prep. 沿着 adv. 向前", "Walk along this street.", "沿着这条街走。", "prep."},
            {"aloud", "əˈlaʊd", "adv. 大声地", "Please read aloud.", "请大声朗读。", "adv."},
            {"already", "ɔːlˈredi", "adv. 已经", "I have already finished my homework.", "我已经完成了作业。", "adv."},
            {"also", "ˈɔːlsoʊ", "adv. 也；同样", "She also likes music.", "她也喜欢音乐。", "adv."},
            {"alter", "ˈɔːltər", "vt. 改变；修改", "You can alter your plans.", "你可以改变计划。", "v."},
            {"alternative", "ɔːlˈtɜːrnətɪv", "n. 替代选择 adj. 替代的", "Is there an alternative solution?", "有别的解决方案吗？", "n."},
            {"although", "ɔːlˈðoʊ", "conj. 虽然；尽管", "Although it was raining, we went out.", "虽然下雨了，我们还是出去了。", "conj."},
            {"always", "ˈɔːlweɪz", "adv. 总是；永远", "He always arrives on time.", "他总是准时到达。", "adv."},
            {"ambition", "æmˈbɪʃən", "n. 野心；抱负", "He has great ambitions.", "他有很大的抱负。", "n."},
            {"ambulance", "ˈæmbjələns", "n. 救护车", "Call an ambulance immediately!", "立刻叫救护车！", "n."},
            {"among", "əˈmʌŋ", "prep. 在...之中", "She is among my best friends.", "她是我最好的朋友之一。", "prep."},
            {"amount", "əˈmaʊnt", "n. 数量；总额 vi. 合计", "A large amount of money was spent.", "花了一大笔钱。", "n."},
            {"amuse", "əˈmjuːz", "vt. 使愉快；逗笑", "The clown amused the children.", "小丑逗乐了孩子们。", "v."},
            {"analyze", "ˈænəlaɪz", "vt. 分析；解析", "We need to analyze the data.", "我们需要分析数据。", "v."},
            {"ancient", "ˈeɪnʃənt", "adj. 古代的；古老的", "This is an ancient temple.", "这是一座古老的寺庙。", "adj."},
            {"anger", "ˈæŋɡər", "n. 愤怒 vt. 使愤怒", "He couldn't hide his anger.", "他无法掩饰他的愤怒。", "n."},
            {"angle", "ˈæŋɡl", "n. 角度；观点", "The angle of the roof is 45 degrees.", "屋顶的角度是45度。", "n."},
            {"angry", "ˈæŋɡri", "adj. 生气的；愤怒的", "She was angry with him.", "她生他的气。", "adj."},
            {"animal", "ˈænɪməl", "n. 动物 adj. 动物的", "Dogs are domestic animals.", "狗是家养动物。", "n."},
            {"announce", "əˈnaʊns", "vt. 宣布；通知", "The government announced new policies.", "政府宣布了新政策。", "v."},
            {"annoy", "əˈnɔɪ", "vt. 使烦恼；打扰", "The noise annoys me.", "噪音让我很烦。", "v."},
            {"annual", "ˈænjuəl", "adj. 年度的；每年的", "This is our annual meeting.", "这是我们的年度会议。", "adj."},
            {"another", "əˈnʌðər", "adj. 另一的 pron. 另一个", "Would you like another cup of tea?", "你想再来一杯茶吗？", "adj."},
            {"answer", "ˈænsər", "n. 回答；答案 v. 回答", "I don't know the answer.", "我不知道答案。", "n."},
            {"anxious", "ˈæŋkʃəs", "adj. 焦虑的；担心的", "She is anxious about the exam.", "她对考试感到焦虑。", "adj."},
            {"any", "ˈeni", "adj. 任何的 pron. 任何一个", "Do you have any questions?", "你有问题吗？", "adj."},
            {"anybody", "ˈenibɒdi", "pron. 任何人", "Is anybody home?", "有人在家吗？", "pron."},
            {"anyone", "ˈeniwʌn", "pron. 任何人", "Can anyone help me?", "有人能帮我吗？", "pron."},
            {"anything", "ˈeniθɪŋ", "pron. 任何事", "You can say anything you want.", "你想说什么都可以。", "pron."},
            {"anyway", "ˈeniweɪ", "adv. 无论如何；反正", "Anyway, let's get started.", "无论如何，我们开始吧。", "adv."},
            {"anywhere", "ˈeniwer", "adv. 在任何地方", "You can sit anywhere you like.", "你可以坐在任何你喜欢的地方。", "adv."},
            {"apart", "əˈpɑːrt", "adv. 分开；相隔", "The two buildings are 100 meters apart.", "两座建筑相距100米。", "adv."},
            {"apartment", "əˈpɑːrtmənt", "n. 公寓", "She lives in a small apartment.", "她住在一间小公寓里。", "n."},
            {"apologize", "əˈpɒlədʒaɪz", "vi. 道歉", "I apologize for being late.", "我为迟到道歉。", "v."},
            {"apparent", "əˈpærənt", "adj. 明显的；表面的", "It is apparent that she is unhappy.", "很明显她不开心。", "adj."},
            {"appeal", "əˈpiːl", "vi. 呼吁；吸引 n. 吸引力", "The idea appeals to me.", "这个主意吸引了我。", "v."},
            {"appear", "əˈpɪr", "vi. 出现；似乎", "The sun appeared from behind the clouds.", "太阳从云层后面出现。", "v."},
            {"appearance", "əˈpɪrəns", "n. 外貌；出现", "Don't judge by appearances.", "不要以貌取人。", "n."},
            {"appetite", "ˈæpɪtaɪt", "n. 食欲；欲望", "I have a good appetite today.", "我今天胃口很好。", "n."},
            {"apple", "ˈæpl", "n. 苹果", "An apple a day keeps the doctor away.", "一天一苹果，医生远离我。", "n."},
            {"application", "ˌæplɪˈkeɪʃən", "n. 申请；应用", "I sent in my job application.", "我递交了工作申请。", "n."},
            {"apply", "əˈplaɪ", "vt. 申请；应用", "I want to apply for this job.", "我想申请这份工作。", "v."},
            {"appoint", "əˈpɔɪnt", "vt. 任命；指定", "They appointed him as manager.", "他们任命他为经理。", "v."},
            {"appointment", "əˈpɔɪntmənt", "n. 约会；任命", "I have a dentist appointment.", "我预约了牙医。", "n."},
            {"appreciate", "əˈpriːʃieɪt", "vt. 欣赏；感激", "I appreciate your help.", "我非常感谢你的帮助。", "v."},
            {"approach", "əˈproʊtʃ", "v. 接近 n. 方法", "Let's approach the problem differently.", "让我们用不同的方法处理这个问题。", "v."},
            {"appropriate", "əˈproʊpriət", "adj. 适当的", "Wear appropriate clothing.", "穿适当的衣服。", "adj."},
            {"approve", "əˈpruːv", "vt. 批准；赞成", "The committee approved the plan.", "委员会批准了这个计划。", "v."},
            {"approximate", "əˈprɒksɪmət", "adj. 大约的 vt. 近似于", "What is the approximate cost?", "大概费用是多少？", "adj."},
            {"architect", "ˈɑːrkɪtekt", "n. 建筑师", "The architect designed this beautiful building.", "建筑师设计了这座美丽的建筑。", "n."},
            {"architecture", "ˈɑːrkɪtektʃər", "n. 建筑学；建筑风格", "She studies architecture.", "她学习建筑学。", "n."},
            {"area", "ˈeriə", "n. 区域；面积", "What is the area of this room?", "这个房间的面积是多少？", "n."},
            {"argue", "ˈɑːrɡjuː", "vi. 争论；辩论", "They are arguing about politics.", "他们在争论政治。", "v."},
            {"argument", "ˈɑːrɡjumənt", "n. 争论；论点", "He presented a strong argument.", "他提出了一个强有力的论点。", "n."},
            {"arise", "əˈraɪz", "vi. 出现；产生", "Problems arose during the project.", "项目进行中出现了问题。", "v."},
            {"arm", "ɑːrm", "n. 手臂；武器 vt. 武装", "She broke her arm.", "她摔断了手臂。", "n."},
            {"army", "ˈɑːrmi", "n. 军队；陆军", "He joined the army.", "他参军了。", "n."},
            {"around", "əˈraʊnd", "prep. 围绕 adv. 周围", "The earth moves around the sun.", "地球围绕太阳转。", "prep."},
            {"arrange", "əˈreɪndʒ", "vt. 安排；整理", "I arranged a meeting for tomorrow.", "我安排了明天的会议。", "v."},
            {"arrangement", "əˈreɪndʒmənt", "n. 安排；布置", "We made arrangements for the trip.", "我们为旅行做了安排。", "n."},
            {"arrest", "əˈrest", "vt. 逮捕；拘留", "The police arrested the suspect.", "警察逮捕了嫌疑人。", "v."},
            {"arrival", "əˈraɪvəl", "n. 到达；到来", "We awaited his arrival.", "我们等待他的到来。", "n."},
            {"arrive", "əˈraɪv", "vi. 到达；到来", "When will you arrive?", "你什么时候到？", "v."},
            {"arrow", "ˈæroʊ", "n. 箭；箭头", "Follow the arrows.", "跟着箭头走。", "n."},
            {"article", "ˈɑːrtɪkl", "n. 文章；物品", "I read an interesting article.", "我读了一篇有趣的文章。", "n."},
            {"artificial", "ˌɑːrtɪˈfɪʃəl", "adj. 人造的；虚假的", "This flower is artificial.", "这花是人造的。", "adj."},
            {"artist", "ˈɑːrtɪst", "n. 艺术家", "She is a talented artist.", "她是一位有才华的艺术家。", "n."},
            {"artistic", "ɑːrˈtɪstɪk", "adj. 艺术的", "She has artistic talent.", "她有艺术天赋。", "adj."},
            {"aside", "əˈsaɪd", "adv. 在旁边", "Put it aside for later.", "把它放在一边稍后处理。", "adv."},
            {"ask", "æsk", "v. 问；要求", "May I ask a question?", "我可以问一个问题吗？", "v."},
            {"asleep", "əˈsliːp", "adj. 睡着的", "The baby is asleep.", "宝宝睡着了。", "adj."},
            {"aspect", "ˈæspekt", "n. 方面；外观", "We should consider every aspect.", "我们应该考虑每个方面。", "n."},
            {"assemble", "əˈsembl", "vt. 组装；集合", "Let's assemble the furniture.", "让我们组装家具吧。", "v."},
            {"assess", "əˈses", "vt. 评估；评定", "We need to assess the situation.", "我们需要评估情况。", "v."},
            {"assign", "əˈsaɪn", "vt. 分配；指派", "The teacher assigned homework.", "老师布置了作业。", "v."},
            {"assist", "əˈsɪst", "vt. 帮助；协助", "Can you assist me with this?", "你能帮我做这个吗？", "v."},
            {"assistance", "əˈsɪstəns", "n. 帮助；援助", "Thank you for your assistance.", "谢谢你的帮助。", "n."},
            {"associate", "əˈsoʊʃieɪt", "vt. 联系；交往", "I associate this song with summer.", "我把这首歌和夏天联系在一起。", "v."},
            {"association", "əˌsoʊsiˈeɪʃən", "n. 协会；联系", "She is a member of the association.", "她是协会的成员。", "n."},
            {"assume", "əˈsuːm", "vt. 假设；认为", "I assume you are coming.", "我假设你会来。", "v."},
            {"assure", "əˈʃʊr", "vt. 保证；使确信", "I can assure you of our support.", "我保证我们会支持你。", "v."},
            {"astonish", "əˈstɒnɪʃ", "vt. 使惊讶", "The news astonished everyone.", "这个消息让所有人震惊。", "v."},
            {"athlete", "ˈæθliːt", "n. 运动员", "He is a professional athlete.", "他是一名职业运动员。", "n."},
            {"atmosphere", "ˈætməsfɪr", "n. 大气；气氛", "The restaurant has a nice atmosphere.", "这家餐厅氛围很好。", "n."},
            {"attach", "əˈtætʃ", "vt. 附加；系上", "Please attach the file.", "请附加文件。", "v."},
            {"attack", "əˈtæk", "vt. 攻击 n. 攻击", "The enemy attacked at night.", "敌人在夜间发起攻击。", "v."},
            {"attempt", "əˈtempt", "vt. 尝试 n. 试图", "He attempted to climb the mountain.", "他试图攀登那座山。", "v."},
            {"attend", "əˈtend", "vt. 参加；照顾", "Will you attend the meeting?", "你会参加会议吗？", "v."},
            {"attention", "əˈtenʃən", "n. 注意；注意力", "Pay attention to the teacher.", "注意听老师讲课。", "n."},
            {"attitude", "ˈætɪtuːd", "n. 态度；姿势", "She has a positive attitude.", "她有积极的态度。", "n."},
            {"attorney", "əˈtɜːrni", "n. 律师", "He hired an attorney.", "他聘请了一位律师。", "n."},
            {"attract", "əˈtrækt", "vt. 吸引；引起", "The exhibition attracted many visitors.", "展览吸引了许多参观者。", "v."},
            {"attraction", "əˈtrækʃən", "n. 吸引力；吸引物", "This city has many attractions.", "这座城市有很多景点。", "n."},
            {"attractive", "əˈtræktɪv", "adj. 有吸引力的", "She is very attractive.", "她很有魅力。", "adj."},
            {"audience", "ˈɔːdiəns", "n. 观众；听众", "The audience applauded.", "观众鼓掌了。", "n."},
            {"author", "ˈɔːθər", "n. 作者；作家", "Who is the author of this book?", "这本书的作者是谁？", "n."},
            {"authority", "əˈθɔːrəti", "n. 权威；权力", "You should consult the authorities.", "你应该咨询权威人士。", "n."},
            {"automatic", "ˌɔːtəˈmætɪk", "adj. 自动的", "This is an automatic door.", "这是一扇自动门。", "adj."},
            {"available", "əˈveɪləbl", "adj. 可用的；有空的", "Is this product available?", "这个产品有货吗？", "adj."},
            {"average", "ˈævərɪdʒ", "n. 平均 adj. 平均的", "The average temperature is 20 degrees.", "平均温度是20度。", "n."},
            {"avoid", "əˈvɔɪd", "vt. 避免；躲开", "Try to avoid making mistakes.", "尽量避免犯错。", "v."},
            {"awake", "əˈweɪk", "adj. 醒着的 vt. 唤醒", "I am still awake.", "我还醒着。", "adj."},
            {"award", "əˈwɔːrd", "n. 奖项 vt. 授予", "She won the award.", "她获得了奖项。", "n."},
            {"aware", "əˈwer", "adj. 意识到的", "Are you aware of the danger?", "你意识到危险了吗？", "adj."},
            {"away", "əˈweɪ", "adv. 离开；远离", "Go away!", "走开！", "adv."},
            {"awful", "ˈɔːfʊl", "adj. 可怕的；极坏的", "The weather is awful today.", "今天天气很糟糕。", "adj."}
        };
        
        for (String[] w : words) {
            Word word = new Word();
            word.setWord(w[0]);
            word.setPronunciation(w[1]);
            word.setMeaning(w[2]);
            word.setExample(w[3]);
            word.setExampleTranslation(w[4]);
            word.setWordType(w[5]);
            wordRepository.save(word);
        }
    }
}
