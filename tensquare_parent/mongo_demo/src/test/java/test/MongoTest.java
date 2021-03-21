package test;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MongoTest {

    //提取变量
    //客户端
    private MongoClient mongoClient;
    //集合
    private MongoCollection<Document> comment;

    @Before
    public void init() {
        //1、创建操作MongoDB的客户端
        mongoClient = new MongoClient("192.168.14.3");
        //MongoClient mongoClient = new MongoClient("192.168.14.3", 27017);
        //2、选择数据库   use commentdb
        MongoDatabase commentdb = mongoClient.getDatabase("commentdb");
        //3、获取集合    db.comment
        comment = commentdb.getCollection("comment");
    }

    //查询所有数据db.comment.find()
    @Test
    public void test1() {
        //4、使用集合进行查询，查询所有数据 db.comment.find()
        FindIterable<Document> documents = comment.find();
        //5、解析结果集（打印）
        //_id:"1", content:"我是张三",userid:"1011",thumbup:2020
        for (Document document : documents) {
            System.out.println("---------------------------");
            System.out.println("_id:" + document.get("_id"));
            System.out.println("content:" + document.get("content"));
            System.out.println("userid:" + document.get("userid"));
            System.out.println("thumbup:" + document.get("thumbup"));
        }
    }

    //提取释放资源代码
    @After
    public void after() {
        //释放资源
        mongoClient.close();
    }

    //根据条件_id查询数据，db.comment.find({"_id":"1"})
    @Test
    public void test2(){
        //创建一个bson对象，封装查询条件
        BasicDBObject bson = new BasicDBObject("_id", "1");
        //将bson作为条件，执行查询
        FindIterable<Document> documents = comment.find(bson);
        for (Document document : documents) {
            System.out.println("---------------------------");
            System.out.println("_id:" + document.get("_id"));
            System.out.println("content:" + document.get("content"));
            System.out.println("userid:" + document.get("userid"));
            System.out.println("thumbup:" + document.get("thumbup"));
        }
    }

    //新增 db.comment.insert({_id:"5", content:"我是田七",userid:"1015",thumbup:666})
    @Test
    public void test3() {
        //封装新增的数据
        Map<String, Object> map = new HashMap<>();
        map.put("_id", "5");
        map.put("content", "我是田七");
        map.put("userid", "1015");
        map.put("thumbup", "666");
        //封装新增文档对象
        Document document = new Document(map);
        //新增一条数据
        comment.insertOne(document);
    }

    //修改 db.comment.update({"_id":5}, {$set:{"thumbup":"888"}})
    @Test
    public void test4() {
        //创建修改的条件 filter
        BasicDBObject filter = new BasicDBObject("_id", "5");
        //创建修改的值 update
        BasicDBObject update = new BasicDBObject("$set", new Document("thumbup", "888"));
        //修改单条数据
        comment.updateOne(filter, update);
    }

    //删除 db.comment.remove({"_id":"5"})
    @Test
    public void test5() {
        //封装删除条件
        BasicDBObject bson = new BasicDBObject("_id","5");
        //删除一条数据
        comment.deleteOne(bson);
    }
}
