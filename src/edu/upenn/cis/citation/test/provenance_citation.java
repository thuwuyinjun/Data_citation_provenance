package edu.upenn.cis.citation.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import org.json.JSONException;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.init.init;
import edu.upenn.cis.citation.pre_processing.view_operation;
import edu.upenn.cis.citation.prov_reasoning.Prov_reasoning4;
import edu.upenn.cis.citation.user_query.query_storage;
import edu.upenn.cis.citation.views.Single_view;

public class provenance_citation {
  
//  static String path = "/home/wuyinjun/workspace/Data_citation_demo/reasoning_results/";
  
  static String path = "reasoning_results/";
  
  public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
  {
    
       
    Connection c = null;
    PreparedStatement pst = null;
  Class.forName("org.postgresql.Driver");
  c = DriverManager
      .getConnection(init.db_url, init.usr_name , init.passwd);
  
  Connection c2 = DriverManager
      .getConnection(init.db_url2, init.usr_name , init.passwd);
    
//    Vector<Query> views = Load_views_and_citation_queries.get_views("views", c, pst);
//    
//    for(int i = 0; i<views.size(); i++)
//    {
//      Query view = views.get(i);
//      
//      Single_view view_obj = new Single_view(view, view.name, c, pst);
//      
//      view_objs.add(view_obj);
//    }
  
    boolean iscluster = Boolean.valueOf(args[0]);
    
    boolean sortcluster = false;//Boolean.valueOf(args[1]);
    
    int factor = 2;//Integer.valueOf(args[1]);
    
    Prov_reasoning4.factor = factor;
    
    Prov_reasoning4.sort_cluster = sortcluster;
  
//  view_operation.delete_view_by_name("v14_1", c, pst);
//  
//  view_operation.delete_view_by_name("v14_2", c, pst);

  
    Prov_reasoning4.init_from_database(c, pst);
    
    
    
//    Vector<Query> query = Load_views_and_citation_queries.get_views(path + "user_queries", c, pst);
    
    Query query = query_storage.get_query_by_id(1, c2, pst);
    
    
    HashMap<Single_view, HashSet<Tuple>> curr_valid_view_mappings = new HashMap<Single_view, HashSet<Tuple>>();
    
    double start = 0;
    
    double end = 0;
    
    start = System.nanoTime();
    
    HashSet<citation_view_vector> covering_sets = Prov_reasoning4.reasoning(query, curr_valid_view_mappings, iscluster, c, pst);
    
    end = System.nanoTime();
    
    double time = (end - start)*1.0/1000000000;
    
//    if(multi_thread)
//      System.out.println("reasoning time 2:" + time);
//    else
    
    if(iscluster)
    {
      System.out.println("reasoning time 3:" + time);
      
      System.out.println("view_mapping_time 3:" + Prov_reasoning4.view_mapping_time);
      
      System.out.println("covering_set_time 3:" + Prov_reasoning4.covering_set_time);

    }
    else
    {
      System.out.println("reasoning time 4:" + time);
      
      System.out.println("view_mapping_time 4:" + Prov_reasoning4.view_mapping_time);
      
      System.out.println("covering_set_time 4:" + Prov_reasoning4.covering_set_time);

    }
    
//    System.out.println(Prov_reasoning4.group_covering_sets.size());
//    
//    System.out.println(Prov_reasoning4.group_view_mappings);

//    output_view_mappings_per_group();
//    
//    output_view_mapping_valid_rids();
        
    if(iscluster)
      write2file(path + "covering_sets3", covering_sets);
    else
      write2file(path + "covering_sets4", covering_sets);
    
//    System.out.println(covering_sets);
//    
//    System.out.println(covering_sets.size());
    
    System.out.println(Prov_reasoning4.rows);
    
    
    System.out.println(Prov_reasoning4.group_view_mappings);
    
    HashSet<String> formatted_citations = Prov_reasoning4.gen_citations(curr_valid_view_mappings, covering_sets, c, pst);
    
    write2file(path + "citation2", formatted_citations);
    
    write2file(path + "covering_sets_per_group2", Prov_reasoning4.group_covering_sets);
    
    
    c.close();
    
    c2.close();
  }
  
  static void output_view_mapping_valid_rids()
  {
    Set<Tuple> tuples = Prov_reasoning4.tuple_valid_rows.keySet();
    
    for(Tuple tuple: tuples)
    {
      System.out.print(tuple.name + "   " + Prov_reasoning4.tuple_valid_rows.get(tuple).size());
      
      System.out.println();
    }
  }
  
  static void output_view_mappings_per_group()
  {
    Set<String> strings = Prov_reasoning4.group_view_mappings.keySet();
    
    int num = 0;
    
    for(String string: strings)
    {
      System.out.println("group" + num);
      
      HashMap<Tuple, Integer> tuple_indexes = Prov_reasoning4.group_view_mappings.get(string);
      
      Set<Tuple> tuples = tuple_indexes.keySet();
      
      String[] tuple_strs = new String[tuples.size()];
      
      int id = 0;
      
      for(Tuple tuple: tuples)
      {
        tuple_strs[id++] = tuple.name;
        
      }

      Arrays.sort(tuple_strs);
      
      for(String tuple_str: tuple_strs)
      {
        System.out.print(tuple_str + "  ");
      }
      
      System.out.println();
      
      num++;
      
    }
  }
  
  public static void write2file(String file_name, HashMap<String, HashSet<citation_view_vector>> views) throws IOException
  {
    File fout = new File(file_name);
    FileOutputStream fos = new FileOutputStream(fout);
 
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
 
    Set<String> group_label = views.keySet();
    
    int num = 0;
    
    for(String label: group_label)
    {
      bw.write("group " + num);
      bw.newLine();
      
      HashSet<citation_view_vector> covering_sets = views.get(label);
      
      String [] covering_set_string = new String [covering_sets.size()];
      
      int id = 0;
      
      for(citation_view_vector covering_set: covering_sets)
      {
        covering_set_string[id ++] = covering_set.toString(); 
      }
      
      Arrays.sort(covering_set_string);
      
      for(String covering_set_str: covering_set_string)
      {
        bw.write(covering_set_str);
        bw.newLine();
      }
      
      num++;
      
    }
    
    bw.close();

  }
  
  public static void write2file(String file_name, HashSet views) throws IOException
  {
      File fout = new File(file_name);
      FileOutputStream fos = new FileOutputStream(fout);
   
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
   
      for (Object view: views) {
          bw.write(view.toString());
          bw.newLine();
      }
   
      bw.close();
  }

}
