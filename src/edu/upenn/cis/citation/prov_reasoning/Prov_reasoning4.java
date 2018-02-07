package edu.upenn.cis.citation.prov_reasoning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.json.JSONException;
//import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
//import com.apporiented.algorithm.clustering.Cluster;
//import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
//import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.CoreCover;
import edu.upenn.cis.citation.Corecover.Database;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.gen_citations.Gen_citation;
import edu.upenn.cis.citation.init.MD5;
import edu.upenn.cis.citation.init.init;
import edu.upenn.cis.citation.multi_thread.Calculate_covering_sets;
import edu.upenn.cis.citation.multi_thread.Calculate_covering_sets_first_round;
import edu.upenn.cis.citation.multi_thread.Check_valid_view_mappings;
import edu.upenn.cis.citation.pre_processing.view_operation;
import edu.upenn.cis.citation.views.Query_converter;
import edu.upenn.cis.citation.views.Single_view;
import fr.lri.tao.apro.ap.Apro;
import fr.lri.tao.apro.ap.AproBuilder;
import fr.lri.tao.apro.data.DataProvider;
import fr.lri.tao.apro.data.MatrixProvider;

public class Prov_reasoning4 {
  
  
public static Vector<Single_view> view_objs = new Vector<Single_view>();
  
  
  static HashMap<String, Vector<String>> rel_attr_mappings = new HashMap<String, Vector<String>>();
  
  static HashMap<String, Head_strs> tuples = new HashMap<String, Head_strs>();
  
  public static boolean test_case = true;
  
  public static double view_mapping_time = 0.0;
  
  public static double covering_set_time = 0.0;
  
  static int gap = 5;
  
  public static int rows = 0;
  
  public static HashMap<Head_strs, ArrayList<Integer>> tuple_why_prov_mappings = new HashMap<Head_strs, ArrayList<Integer>>();
  
  public static ArrayList<Vector<Head_strs>> all_why_tokens = new ArrayList<Vector<Head_strs>>();
  
  public static HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings = new HashMap<Single_view, HashSet<Tuple>>();
  
  public static ResultSet rs = null;
  
  public static HashMap<String, HashMap<String, Vector<Integer>>> relation_attribute_value_mappings = new HashMap<String, HashMap<String, Vector<Integer>>>();

  public static HashMap<Tuple, HashSet<Integer>> tuple_valid_rows = new HashMap<Tuple, HashSet<Integer>>();
  
  public static boolean agg_intersection = true;
  
  public static HashMap<String, Integer> max_num = new HashMap<String, Integer>();
  
  public static ArrayList<HashSet<Tuple>> valid_view_mappings_schema_level = new ArrayList<HashSet<Tuple>>();
  
  static HashMap<String, Integer> query_subgoal_id_mappings = new HashMap<String, Integer>();
  
  static HashMap<String, Integer> query_relation_attr_id_mappings = new HashMap<String, Integer>();

  static HashMap<Argument, Integer> query_arg_id_mappings = new HashMap<Argument, Integer>();
  
  static HashMap<String, HashSet<Integer>> group_ids = new HashMap<String, HashSet<Integer>>();
  
  public static HashMap<String, HashMap<Tuple, Integer>> group_view_mappings = new HashMap<String, HashMap<Tuple, Integer>>();

  public static HashMap<String, HashSet<citation_view_vector>> group_covering_sets = new HashMap<String, HashSet<citation_view_vector>>();
  
  static ArrayList<HashSet<Tuple>> valid_view_mappings_per_head_var = new ArrayList<HashSet<Tuple>>();
  
  public static boolean sort_cluster = false;
  
  public static int factor = 1;
  
  public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException
  {
    Vector<Integer> index = new Vector<Integer>();
    
//    String string1 = "11";
//    
//    String string2 = "9";
//    
//    System.out.println(string1.compareTo(string2));
    
    Connection c = null;
    PreparedStatement pst = null;
  Class.forName("org.postgresql.Driver");
  c = DriverManager
      .getConnection(init.db_url, init.usr_name , init.passwd);
    
//  view_operation.delete_view_by_name("v7", c, pst, false);
  
  view_operation.delete_view_by_name("v5", c, pst, false);
//    Vector<Query> views = Load_views_and_citation_queries.get_views("views", c, pst);
//    
//    test_case = false;
//    
//    for(int i = 0; i<views.size(); i++)
//    {
//      Query view = views.get(i);
//      
//      Single_view view_obj = new Single_view(view, view.name, c, pst);
//      
//      view_objs.add(view_obj);
//    }
//    
//    HashMap<Single_view, HashSet<Tuple>> curr_valid_view_mappings = new HashMap<Single_view, HashSet<Tuple>>();
//    
//    Vector<Query> query = Load_views_and_citation_queries.get_views("query", c, pst);
//    
//    HashSet<citation_view_vector> covering_sets = reasoning(query.get(0), curr_valid_view_mappings, c, pst);
//    
//    System.out.println(covering_sets);
    
    c.close();
    
  }
  
  public static void init_from_files(String file_name, Connection c, PreparedStatement pst) throws SQLException
  {
    Vector<Query> views = Load_views_and_citation_queries.get_views(file_name, c, pst);
    
    for(int i = 0; i<views.size(); i++)
    {
      Query view = views.get(i);
      
      Single_view view_obj = new Single_view(view, view.name, c, pst);
      
      view_objs.add(view_obj);
    }
  }

  public static void init_from_database(Connection c, PreparedStatement pst) throws SQLException
  {
    Vector<Query> views = get_all_views(c, pst);
    
    for(int i = 0; i<views.size(); i++)
    {
      Single_view curr_view_obj = new Single_view(views.get(i), views.get(i).name, c, pst);
      
      view_objs.add(curr_view_obj);
    }
  }
  
  
  static Vector<Query> get_all_views(Connection c, PreparedStatement pst) throws SQLException
  {
    return view_operation.get_all_views(c, pst);
  }
  
  
  static void clone_view_mappings(HashMap<Single_view, HashSet<Tuple>> view_mappings, HashMap<Single_view, HashSet<Tuple>> view_mappings_copy)
  {
    Set<Single_view> views = view_mappings.keySet();
    
    for(Iterator iter = views.iterator(); iter.hasNext();)
    {
      Single_view view = (Single_view) iter.next();
      
      HashSet<Tuple> tuples = view_mappings.get(view);
      
      view_mappings_copy.put(view, (HashSet<Tuple>) tuples.clone());
      
    }
  }
  
  
  
  
//  static ArrayList<HashMap<Single_view, HashSet<Tuple>>> reasoning_covering_sets_conjunctive_query(Query user_query, Connection c, PreparedStatement pst) throws SQLException
//  {
//    HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings = get_all_possible_view_mappings(user_query);
//    
//    String sql = Query_converter.data2sql_with_token_columns(user_query);
//    
//    pst = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//    
//    ResultSet rs = pst.executeQuery();
//    
//    ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings_per_head_var = new ArrayList<HashMap<Single_view, HashSet<Tuple>>>();
//    
//    int num = 0;
//    
//    while(rs.next())
//    {
//      
//      if(num == 0)
//      {        
//        for(int i = 0; i<user_query.head.args.size(); i++)
//        {
//          valid_view_mappings_per_head_var.add(clone_view_mappings(all_possible_view_mappings));
//        }
//      }
//      
//      Vector<String> why_tokens = new Vector<String>();
//      
//      for(int i = 0; i < user_query.body.size(); i++)
//      {
//        why_tokens.add(rs.getString(user_query.head.args.size() + i + 1));
//        
//      }
//      
//      checking_why_provenance_tokens(why_tokens, all_possible_view_mappings);
//      
//      for(int i = 0; i<user_query.head.args.size(); i++)
//      {
//        String where_token = rs.getString(i + 1);
//        
//        check_where_tokens(where_token, valid_view_mappings_per_head_var.get(i));
//        
//        checking_where_why_provenance_tokens(valid_view_mappings_per_head_var.get(i), where_token, why_tokens, all_possible_view_mappings);
//      }
//      
//      num++;
//      
//    }
//    
//    return valid_view_mappings_per_head_var;
//    
//  }
//  
  static Vector<String> get_curr_where_token_seq(ResultSet rs, Vector<String> where_token_seqs, int subgoal_size, int head_arg_size) throws SQLException
  {
    
    Vector<String> where_tokens = new Vector<String>();
    
    for(int i = 0; i<head_arg_size; i++)
    {
      String where_token = rs.getString(i+1);
      
      where_token = where_token.replaceAll("\\|", "\\\\|");
      
      where_tokens.add(where_token);
      
      if(i >= where_token_seqs.size())
      {
        
        //(?=.*\bjack\b)
        String where_token_seq = "^(?=.*\\b" + where_token + "\\b)";
        
        where_token_seqs.add(where_token_seq);
        
      }
      else
      {
        String where_token_seq = where_token_seqs.get(i);
        
        where_token_seq += "(?=.*\\b" + where_token + "\\b)";
        
        where_token_seqs.set(i, where_token_seq);
        
      }
    }
    
    return where_tokens;
  }
  
  static void init_view_mappings_conjunctive_query(ArrayList<HashSet<Tuple>> valid_view_mappings_per_head_var, Vector<Argument> head_args, HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings)
  {    
    for(int i = 0; i<head_args.size(); i++)
    {
      HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings_copy = new HashMap<Single_view, HashSet<Tuple>>(); 
      
      clone_view_mappings(all_possible_view_mappings, all_possible_view_mappings_copy);
      
      Set<Single_view> views = all_possible_view_mappings_copy.keySet();
      
      Argument arg = head_args.get(i);
            
      String arg_rel_name = arg.relation_name;
      
      HashSet<Tuple> possible_view_mappings_per_head_var = new HashSet<Tuple>();
      
      for(Iterator iter = views.iterator(); iter.hasNext();)
      {
        Single_view view = (Single_view) iter.next();
        
        HashSet<Tuple> tuples = all_possible_view_mappings_copy.get(view);
        
        for(Iterator iter2 = tuples.iterator(); iter2.hasNext();)
        {
          Tuple tuple = (Tuple) iter2.next();
          
          if(!tuple.args.contains(arg))
          {
            iter2.remove();
          }
          
//          if(i == 0)
//          {
//            HashSet<String> target_subgoal_names = tuple.getTargetSubgoal_strs();
//            
//            tuple_targeted_subgoal_mappings.put(tuple, target_subgoal_names);
//            
//            if(!tuple.args.contains(arg))
//            {
//              iter2.remove();
//            }
//          }
//          else
//          {
//            HashSet<String> target_subgoal_names = tuple_targeted_subgoal_mappings.get(tuple);
//            
//            if(!target_subgoal_names.contains(arg_rel_name))
//            {
//              iter2.remove();
//            }
//          }
          
        }
        
        if(tuples.isEmpty())
          iter.remove();
        
        possible_view_mappings_per_head_var.addAll(tuples);
        
      }
      
      if(views.isEmpty())
      {
        continue;
      }
      
      valid_view_mappings_per_head_var.add(possible_view_mappings_per_head_var);
      
    }
    
    
    
    
    
  }
  
  static void add_suffix_token_seq(Vector<String> token_seqs)
  {
    for(int i = 0; i<token_seqs.size(); i++)
    {
      String token_seq = token_seqs.get(i);
      
      token_seq += ".*$";
      
      token_seqs.set(i, token_seq);
    }
  }
  
  
  static void input_single_relation(HashMap<String, Integer> relation_attr_id_mappings, String relation, Connection c, PreparedStatement pst) throws SQLException
  {
    Vector<String> attributes = init.get_attributes_single_relation(relation, c, pst);
    
    rel_attr_mappings.put(relation, attributes);
    
    String query = "select ";//* from " + relation;
    
    for(int i = 0; i<attributes.size(); i++)
    {
      if(i >= 1)
        query += ",";
      
      query += attributes.get(i);
    }
    
    String col_name_encoding = MD5.get_MD5_encoding(relation);
    
    String prov_col_name = "c" + init.separator + col_name_encoding + init.provenance_column_suffix;

    query += ",\"" + prov_col_name + "\"";
    
    query += " from " + relation;
    
    pst = c.prepareStatement(query);
    
    ResultSet rs = pst.executeQuery();
    
    
    
    
    ResultSetMetaData meta_data = rs.getMetaData();
    
    for(int i = 0; i<meta_data.getColumnCount(); i++)
    {
      String attr_name = meta_data.getColumnLabel(i + 1);
      
      relation_attr_id_mappings.put(relation + init.separator + attr_name, i);
    }
    
    
    while(rs.next())
    {
      Vector<String> values = new Vector<String>();
      
      for(int i = 0; i<attributes.size(); i++)
      {
        String value = rs.getString(attributes.get(i));
        
        values.add(value);
        
      }
      
      Head_strs tuple = new Head_strs(values);
      
      String token = rs.getString(prov_col_name);
      
      tuples.put(token, tuple);
    }
    
  }
  
  static void input_relations(HashMap<String, Integer> relation_attr_id_mappings, HashSet<String> tables, Connection c, PreparedStatement pst) throws SQLException
  {
    for(Iterator it = tables.iterator(); it.hasNext();)
    {
      String relation = (String) it.next();
      
      input_single_relation(relation_attr_id_mappings, relation, c, pst);
      
    }
  }
  
  static Vector<Head_strs> get_tuples(ResultSet rs, Vector<Argument> args, Vector<Subgoal> subgoals) throws SQLException
  {
    Vector<Head_strs> curr_tuples = new Vector<Head_strs>();
    
    for(int i = args.size(); i < subgoals.size() + args.size(); i++)
    {
      String token = rs.getString(i + 1);
      
      Head_strs curr_tuple = tuples.get(token);
      
      curr_tuples.add(curr_tuple);
    }
    
    return curr_tuples;
    
  }
  
  static void check_valid_view_mappings(Vector<Head_strs> curr_tuples, HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings)
  {
    Set<Single_view> views = all_possible_view_mappings.keySet();
    
    for(Iterator iter = views.iterator(); iter.hasNext();)
    {
      Single_view view = (Single_view) iter.next();
      
      
      
    }
  }
  
  static HashMap<Tuple, Integer> evaluate_views(ArrayList<Vector<Head_strs>> curr_tuples, HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings, HashMap<Tuple, Integer> tuple_ids, Query user_query, Connection c, PreparedStatement pst) throws InterruptedException
  {
    Set<Single_view> views = all_possible_view_mappings.keySet();
        
    Vector<Check_valid_view_mappings> check_threads = new Vector<Check_valid_view_mappings>();
    
    for(Iterator iter = views.iterator(); iter.hasNext();)
    {
      Single_view view = (Single_view) iter.next();
      
      HashSet<Tuple> tuples = all_possible_view_mappings.get(view);
      
      Check_valid_view_mappings check_thread = new Check_valid_view_mappings(view.view_name, view, tuples, curr_tuples, relation_attribute_value_mappings, c, pst);
      
      check_thread.start();
      
      check_threads.add(check_thread);
      
    }
    
    for(int i = 0; i<check_threads.size(); i++)
    {
      check_threads.get(i).join();
    }
    
    for(int i = 0; i<check_threads.size(); i++)
    {
      tuple_valid_rows.putAll(check_threads.get(i).tuple_rows);
    }
    
    int id = 0;
    
    for(Iterator iter = views.iterator(); iter.hasNext();)
    {
      Single_view view = (Single_view) iter.next();
      
      HashSet<Tuple> tuples = all_possible_view_mappings.get(view);
      
      if(agg_intersection)
      {
        Iterator it = tuples.iterator();
        
        while(it.hasNext())
        {
          Tuple tuple = (Tuple)it.next();
          
          if(tuple_valid_rows.get(tuple).size() < rows)
          {
            it.remove();
          }
          else
          {
            tuple_ids.put(tuple, id);
            
            id++;
          }
        }
      }
      else
      {
        Iterator it = tuples.iterator();
        
        while(it.hasNext())
        {
          Tuple tuple = (Tuple) it.next();
          
          if(tuple_valid_rows.get(tuple).isEmpty())
          {
            it.remove();
          }
          else
          {
            tuple_ids.put(tuple, id);
            
            id++;
          }
        }
      }
      
      if(tuples.isEmpty())
        iter.remove();
            
    }
    
    get_valid_view_mappings_per_group();
    
    return tuple_ids;
    
//    for(Iterator iter = views.iterator(); iter.hasNext();)
//    {
//      Single_view view = (Single_view) iter.next();
//      
//      HashSet<Tuple> tuples = all_possible_view_mappings.get(view);
//      
//      for(Iterator iter2 = tuples.iterator(); iter2.hasNext();)
//      {
////        view.reset_values();
//        
//        Tuple tuple = (Tuple) iter2.next();
//        
//        for(int i = 0; i<curr_tuples.size(); i++)
//        {
//          view.evaluate_args(curr_tuples.get(i), tuple);
//          
//          if(!view.check_validity(tuple))
//          {
//            iter2.remove();
//            break;
//          }
//        }
//        
//      }
//      
//      if(tuples.isEmpty())
//        iter.remove();
//      
//    }
  }
  
  static void get_valid_view_mappings_per_group()
  {
    Set<Tuple> view_mappings = tuple_valid_rows.keySet();
    
    ArrayList<HashSet<Integer>> rid_sets = new ArrayList<HashSet<Integer>>();
    
    ArrayList<Tuple> all_view_mappings = new ArrayList<Tuple>();
    
    for(Tuple view_mapping: view_mappings)
    {
      HashSet<Integer> curr_rids = tuple_valid_rows.get(view_mapping);
      
      rid_sets.add(curr_rids);
      
      all_view_mappings.add(view_mapping);
      
    }
        
    for(int i = 0; i<rows; i++)
    {

      String signiture = new String();
      
      ArrayList<Integer> tuple_ids = new ArrayList<Integer>();
      
      for(int j = 0; j< rid_sets.size(); j++)
      {
        if(rid_sets.get(j).contains(i))
        {
          signiture += "," + j;
          
          tuple_ids.add(j);
        }
      }
      
      if(group_ids.get(signiture) == null)
      {
        HashSet<Integer> ids = new HashSet<Integer>();
        
        ids.add(i);
        
        group_ids.put(signiture, ids);
        
        HashMap<Tuple, Integer> curr_view_mappings = new HashMap<Tuple, Integer>();
        
        for(int j = 0; j<tuple_ids.size(); j++)
        {
          curr_view_mappings.put(all_view_mappings.get(tuple_ids.get(j)), j);
        }
        
        group_view_mappings.put(signiture, curr_view_mappings);
      }
      else
      {
        group_ids.get(signiture).add(i);
      }
    }
    
    
    
    
  }
  
  static ArrayList<int[]> get_valid_view_mappings(ArrayList<HashSet<citation_view_vector>> covering_sets_per_attributes, Vector<Argument> args, ArrayList<HashSet<Tuple>> valid_view_mappings_per_head_var, HashMap<Tuple, Integer> tuple_ids)
  {
    
//    System.out.println(tuple_ids);
    
    Set<Tuple> valid_view_mappings_schema_level = tuple_ids.keySet();
    
    HashSet<HashSet<Tuple>> all_tuples = new HashSet<HashSet<Tuple>>();
    
    ArrayList<int[]> tuple_index = new ArrayList<int[]>();
    
    for(int i = 0; i<valid_view_mappings_per_head_var.size(); i++)
    {
      HashSet<Tuple> valid_view_mappings = valid_view_mappings_per_head_var.get(i);
      
      valid_view_mappings.retainAll(valid_view_mappings_schema_level);
      
//      Set<Single_view> views = all_possible_view_mappings.keySet();
//      
//      HashSet<Tuple> curr_tuples = new HashSet<Tuple>();
//            
//      for(Iterator iter = views.iterator(); iter.hasNext();)
//      {
//        Single_view view = (Single_view) iter.next();
//        
//        HashSet<Tuple> tuples1 = valid_view_mappings.get(view);
//        
//        HashSet<Tuple> tuples2 = all_possible_view_mappings.get(view);
//        
//        if(tuples2 == null)
//        {
//          iter.remove();
//          
//          continue;
//        }
//        
//        tuples1.retainAll(tuples2);
//        
//        if(tuples1.isEmpty())
//          iter.remove();
//        
//        curr_tuples.addAll(tuples1);
//        
//      }
      
      if(all_tuples.contains(valid_view_mappings))
      {
        valid_view_mappings_per_head_var.remove(i);
        
        i--;
      }
      else
      {
        all_tuples.add(valid_view_mappings);
        
        int [] curr_tuple_index = new int[tuple_ids.size()];
        
        for(int k = 0; k<curr_tuple_index.length; k++)
        {
          curr_tuple_index[k] = 0;
        }
        
        HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
        
        for(Tuple tuple: valid_view_mappings)
        {
          int id = tuple_ids.get(tuple);
          
          
          curr_tuple_index[id] = 1;
          
          Tuple valid_tuple = (Tuple) tuple.clone();
          
          valid_tuple.args.retainAll(args);
              
          
          long [] tuple_id_contained = new long[(tuple_ids.size() + Long.SIZE - 1)/Long.SIZE];
          
          tuple_id_contained[id/Long.SIZE] |= (1L << (id % Long.SIZE));
          
          if(valid_tuple.lambda_terms.size() > 0)
          {
              
              citation_view_parametered c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple, query_subgoal_id_mappings, query_arg_id_mappings, tuple_ids);
              
              citation_view_vector curr_views = new citation_view_vector(c, tuple_id_contained);
              
              remove_duplicate(curr_covering_sets, curr_views);
              
//              curr_covering_sets.add(curr_views);
              
              
              
          }   
          else
          {
              
              citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple, query_subgoal_id_mappings, query_arg_id_mappings, tuple_ids);
              
              citation_view_vector curr_views = new citation_view_vector(c, tuple_id_contained);
              
              remove_duplicate(curr_covering_sets, curr_views);
          }
        }
        
        covering_sets_per_attributes.add(curr_covering_sets);
        
        tuple_index.add(curr_tuple_index);
      }
    }
    
    return tuple_index;
  }
  
  static Head_strs get_query_result(ResultSet rs, int head_arg_size) throws SQLException
  {
    Vector<String> values = new Vector<String>();
    
    for(int i = 0; i<head_arg_size; i++)
    {
      String value = rs.getString(i + 1);
      
      values.add(value);
    }
    
    Head_strs curr_query_result = new Head_strs(values);
    
    return curr_query_result;
  }
  
  static ArrayList<HashSet<Tuple>> clone_view_mapping_per_attribute(ArrayList<HashSet<Tuple>> view_mappings_per_head_vairable)
  {
    ArrayList<HashSet<Tuple>> view_mappings_per_head_variable_copy = new ArrayList<HashSet<Tuple>>();
    
    for(int i = 0; i<view_mappings_per_head_vairable.size(); i++)
    {
      view_mappings_per_head_variable_copy.add((HashSet<Tuple>) view_mappings_per_head_vairable.get(i).clone());
    }
    
    return view_mappings_per_head_variable_copy;
  }
  
  static double[][] reasoning_valid_view_mappings_conjunctive_query(ArrayList<HashSet<citation_view_vector>> covering_sets_per_attribute, Query user_query, HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings_copy, HashMap<Tuple, Integer> tuple_ids, boolean isclustering, String sql, Connection c, PreparedStatement pst) throws SQLException, InterruptedException
  {
    
    HashSet<String> tables = new HashSet<String>();
    
    for(int i = 0; i<user_query.body.size(); i++)
    {
      Subgoal subgoal = (Subgoal) user_query.body.get(i);
      
      query_subgoal_id_mappings.put(subgoal.name, i);
      
      tables.add(user_query.subgoal_name_mapping.get(subgoal.name));
    }
    
    for(int i = 0; i<user_query.head.args.size(); i++)
    {
      Argument arg = (Argument) user_query.head.args.get(i);
      
      query_arg_id_mappings.put(arg, i);
    }
    
//    System.out.println(user_query);
//    
//    System.out.println(tables);
    
    input_relations(query_relation_attr_id_mappings, tables, c, pst);
    
    all_possible_view_mappings = get_all_possible_view_mappings(query_subgoal_id_mappings, user_query);
    
    clone_view_mappings(all_possible_view_mappings, all_possible_view_mappings_copy);
    
    
    init_view_mappings_conjunctive_query(valid_view_mappings_per_head_var, user_query.head.args, all_possible_view_mappings_copy);    
    
    ArrayList<HashSet<Tuple>> valid_view_mappings_per_head_var_copy = clone_view_mapping_per_attribute(valid_view_mappings_per_head_var);
    
    pst = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    
    ResultSet rs = pst.executeQuery();
    
    while(rs.next())
    {
      
      Vector<Head_strs> curr_tuples = get_tuples(rs, user_query.head.args, user_query.body);
      
      Head_strs values = get_query_result(rs, user_query.head.args.size());
      
      if(tuple_why_prov_mappings.get(values) == null)
      {
        ArrayList<Integer> curr_tokens = new ArrayList<Integer>();
        
        curr_tokens.add(rows);
        
        tuple_why_prov_mappings.put(values, curr_tokens);
        
      }
      else
      {
        tuple_why_prov_mappings.get(values).add(rows);
      }
      
      all_why_tokens.add(curr_tuples);
      
      rows ++;
//      Vector<String> where_tokens = get_curr_where_token_seq(rs, where_token_seqs, subgoal_size, head_var_size);
//      
//      Vector<String> why_tokens = get_curr_why_token_seq(rs, why_token_seqs, subgoal_size, head_var_size, all_possible_view_mappings);
//      
//      get_curr_where_why_token_seq(rs, where_tokens, why_tokens, where_why_token_seqs, subgoal_size, head_var_size, all_possible_view_mappings);
      
    }
    
    evaluate_views(all_why_tokens, all_possible_view_mappings_copy, tuple_ids, user_query, c, pst);
    
    
    ArrayList<int[]> tuple_index = get_valid_view_mappings(covering_sets_per_attribute, user_query.head.args, valid_view_mappings_per_head_var_copy, tuple_ids);
    
//    add_suffix_token_seq(where_token_seqs);
//    
//    check_where_tokens(where_token_seqs, valid_view_mappings_per_head_var, where_why_token_seqs);
//    
//    checking_why_provenance_tokens(why_token_seqs, all_possible_view_mappings);
//
//    
//    checking_where_why_provenance_tokens(valid_view_mappings_per_head_var, where_why_token_seqs, all_possible_view_mappings);
    
    valid_view_mappings_schema_level = valid_view_mappings_per_head_var_copy;
    
    if(isclustering)
      return cal_distances(tuple_index);
    else
      return null;
    
  }
  
  static double get_distance(int[] index1, int []index2)
  {
    double distance = 0;
    
    for(int i = 0; i<index1.length; i++)
    {
      distance += Math.pow((index1[i] - index2[i]), 2);
    }
    
    return -distance;
  }
  
  static double[][] cal_distances(ArrayList<int[]> tuple_index)
  {
    if(tuple_index.size() == 1)
    {
      double[][] distances = new double[tuple_index.size()][tuple_index.size()];
      
      for(int i = 0; i<tuple_index.size(); i++)
      {
        distances[i][i] = 0;
      }
      
      return distances;
    }
    
    
    double[][] distances = new double[tuple_index.size()][tuple_index.size()];
    
    double []similarity_values = new double[tuple_index.size() * tuple_index.size() - tuple_index.size()];
    
    int num = 0;
    
    for(int i = 0; i < tuple_index.size(); i++)
    {
      for(int j = 0; j<tuple_index.size(); j++)
      {
        if(i != j)
        {
          distances[i][j] = get_distance(tuple_index.get(i), tuple_index.get(j));
          
          similarity_values[num++] = distances[i][j];
        }
      }
    }
    
    Arrays.sort(similarity_values);
    
    for(int i = 0; i<tuple_index.size(); i++)
    {
      distances[i][i] = factor * similarity_values[similarity_values.length/2];
    }
    
    return distances;
  }
  
  static ArrayList<Vector<Head_strs>> get_sel_tokens(Vector<Head_strs> sel_values)
  {
    ArrayList<Vector<Head_strs>> all_tokens = new ArrayList<Vector<Head_strs>>();
    
    for(int i = 0; i<sel_values.size(); i++)
    {
      
      ArrayList<Integer> row_ids = tuple_why_prov_mappings.get(sel_values.get(i));
      
      for(int j = 0; j<row_ids.size(); j++)
      {
        all_tokens.add(all_why_tokens.get(row_ids.get(j)));
      }
      
    }
    
    return all_tokens;
  }
  
  static HashSet<citation_view_vector> reasoning_multi_tuples(Vector<Head_strs> values, Query user_query, Connection c, PreparedStatement pst) throws SQLException, InterruptedException
  {
    ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings = reasoning_valid_view_mappings_conjunctive_query_multi_tuples(user_query, values, c, pst);
    
    HashSet<citation_view_vector> covering_sets = reasoning_covering_set_multi_threads_multi_hops_conjunctive_query(valid_view_mappings, user_query.head.args, true);

    return covering_sets;
    
  }
  
  static ArrayList<HashMap<Single_view, HashSet<Tuple>>> reasoning_valid_view_mappings_conjunctive_query_multi_tuples(Query user_query, Vector<Head_strs> sel_values, Connection c, PreparedStatement pst) throws SQLException, InterruptedException
  {    
    HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings_copy = new HashMap<Single_view, HashSet<Tuple>>(); 
        
    clone_view_mappings(all_possible_view_mappings, all_possible_view_mappings_copy);
        
    ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings_per_head_var = new ArrayList<HashMap<Single_view, HashSet<Tuple>>>();
    
//    init_view_mappings_conjunctive_query(valid_view_mappings_per_head_var, user_query.head.args, all_possible_view_mappings);
//    
//    ArrayList<Vector<Head_strs>> all_tuples = get_sel_tokens(sel_values);//new ArrayList<Vector<Head_strs>>();
//
////    HashMap<Tuple, Integer> tuple_ids = evaluate_views(all_tuples, all_possible_view_mappings_copy, c, pst);
//    
//    ArrayList<HashSet<citation_view_vector>> covering_sets_per_attributes = new ArrayList<HashSet<citation_view_vector>>();
    
//    ArrayList<int[]> tuple_index = get_valid_view_mappings(covering_sets_per_attributes, user_query.head.args, all_possible_view_mappings_copy, valid_view_mappings_per_head_var, tuple_ids);
    
    return valid_view_mappings_per_head_var;
    
  }
  
  public static HashSet<citation_view_vector> reasoning(Query user_query, HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings_copy, boolean ifclustering, Connection c, PreparedStatement pst) throws SQLException, InterruptedException
  {
    String sql = new String();
    
    
    if(!test_case)
      sql = Query_converter.data2sql_with_why_token_columns(user_query);
    else
      sql = Query_converter.data2sql_with_why_token_columns_test(user_query);
      
    
    
    
    double start = 0.0;
    
    double end = 0.0;
    
    
    
    start = System.nanoTime();
    
    ArrayList<HashSet<citation_view_vector>> covering_sets_per_attributes = new ArrayList<HashSet<citation_view_vector>>();
    
    HashMap<Tuple, Integer> tuple_ids = new HashMap<Tuple, Integer>();
    
    double[][] distances = reasoning_valid_view_mappings_conjunctive_query(covering_sets_per_attributes, user_query, all_possible_view_mappings_copy, tuple_ids, ifclustering, sql, c, pst);
    
    end = System.nanoTime();
    
    view_mapping_time = (end - start) * 1.0/1000000000;
    
    start = System.nanoTime();
    
    HashSet<citation_view_vector> covering_sets;
    
    if(ifclustering)
      covering_sets = reasoning_covering_set_ap(distances, covering_sets_per_attributes, tuple_ids);
    else
    {
      covering_sets = reasoning_covering_sets(distances, covering_sets_per_attributes, tuple_ids);
    }
    
    end = System.nanoTime();
    
    covering_set_time = (end - start) * 1.0/1000000000;
    
    cal_covering_sets_per_group(tuple_ids, covering_sets, user_query);
    
    return covering_sets;
  }
  
  static void cal_covering_sets_per_group(HashMap<Tuple, Integer> tuple_ids_schema_level, HashSet<citation_view_vector> covering_set_schema_level, Query user_query)
  {
    Set<String> signature = group_view_mappings.keySet();
    
    for(String curr_signature : signature)
    {
      HashMap<Tuple, Integer> curr_tuple_ids = group_view_mappings.get(curr_signature);
      
      Set<Tuple> curr_valid_tuples = curr_tuple_ids.keySet();
      
      Set<Tuple> valid_tuples_schema_level = tuple_ids_schema_level.keySet();
      
      ArrayList<HashSet<citation_view_vector>> covering_sets_per_attribute = new ArrayList<HashSet<citation_view_vector>>();
      
      if(curr_valid_tuples.containsAll(valid_tuples_schema_level) && valid_tuples_schema_level.containsAll(curr_valid_tuples))
      {
        group_covering_sets.put(curr_signature, covering_set_schema_level);
      }
      else
      {
        
        ArrayList<HashSet<Tuple>> valid_view_mappings_per_head_var_copy = clone_view_mapping_per_attribute(valid_view_mappings_per_head_var);
        
        ArrayList<int[]> tuple_index = get_valid_view_mappings(covering_sets_per_attribute, user_query.head.args, valid_view_mappings_per_head_var_copy, curr_tuple_ids);
        
        double[][] distances = cal_distances(tuple_index);
        
        HashSet<citation_view_vector> curr_group_covering_sets = reasoning_covering_set_ap(distances, covering_sets_per_attribute, curr_tuple_ids);
        
        group_covering_sets.put(curr_signature, curr_group_covering_sets);

      }
      
      
    }
    
  }
  
  public static HashSet<String> gen_citations(HashMap<Single_view, HashSet<Tuple>> all_possible_view_mappings_copy, HashSet<citation_view_vector> covering_sets, Connection c, PreparedStatement pst) throws SQLException, JSONException
  {
    HashSet<String> formatted_citations = Gen_citation.gen_citation_entire_query(all_possible_view_mappings_copy, covering_sets, tuple_valid_rows, all_why_tokens, max_num, c, pst);
    
    return formatted_citations;
  }
  
  static HashSet<citation_view_vector> reasoning_covering_set_conjunctive_query(ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings_per_head_var, Vector<Argument> args, HashMap<Tuple, Integer> tuple_ids)
  {
    
    int loop_time = (int) Math.ceil(Math.log(valid_view_mappings_per_head_var.size())/Math.log(2));
    
    Vector<HashSet<citation_view_vector>> covering_sets = new Vector<HashSet<citation_view_vector>>();
    
    for(int i = 1; i<=loop_time; i++)
    {
      int j = 0;
      
      if(i == 1)
      {
        
        for(j = 0; j<valid_view_mappings_per_head_var.size() + 2*i; j = j+2*i)
        {
          HashSet<citation_view_vector> view_com = new HashSet<citation_view_vector>();
          
          for(int k = j; k<j+2*i && k < valid_view_mappings_per_head_var.size(); k++)
          {
            HashMap<Single_view, HashSet<Tuple>> valid_view_mappings = valid_view_mappings_per_head_var.get(k);
            
            Set<Single_view> views = valid_view_mappings.keySet();
            
            HashSet<Tuple> all_tuples = new HashSet<Tuple>();
            
            for(Iterator iter = views.iterator(); iter.hasNext();)
            {
              Single_view view = (Single_view) iter.next();
              
              HashSet<Tuple> tuples = valid_view_mappings.get(view);
              
              all_tuples.addAll(tuples);
              
              
            }
            
            view_com = join_views_curr_relation(all_tuples, view_com, args, tuple_ids);
            
          }
          
          if(!view_com.isEmpty())
             covering_sets.add(view_com);
        }
        
        
      }
      else
      {
        int merge_times = (int) Math.ceil(valid_view_mappings_per_head_var.size()/(2*i));
        
        for(int k = 0; k<covering_sets.size(); k=k+2)
        {
          if(k + 1 < covering_sets.size())
          {
            HashSet<citation_view_vector> updated_covering_set = join_operation(covering_sets.get(k), covering_sets.get(k + 1));
            
            covering_sets.set(k/2, updated_covering_set);
          }
          else
          {
            covering_sets.set(k/2, covering_sets.get(k));
          }
        }
        
        int redundant_start = (covering_sets.size() + 1)/2;
        
        int redundant_end = covering_sets.size();
        
        for(int k = redundant_start; k<redundant_end; k++)
        {
          covering_sets.remove(covering_sets.size() - 1);
        }
      }
    }
    
    return covering_sets.get(0);
  }
  
  static HashSet<citation_view_vector> reasoning_covering_set_multi_threads_multi_hops_conjunctive_query(ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings_per_head_var, Vector<Argument> args, boolean multi_thread) throws InterruptedException
  {
    
//    System.out.println("multi_thread");
    
    int loop_time = (int) Math.ceil(Math.log(valid_view_mappings_per_head_var.size())/Math.log(gap));
    
    ArrayList<HashSet<citation_view_vector>> covering_sets = new ArrayList<HashSet<citation_view_vector>>();
    
    for(int i = 1; i<=loop_time; i++)
    {
      int j = 0;
      
      if(i == 1)
      {
        
        ArrayList<Calculate_covering_sets_first_round> cal_threads = new ArrayList<Calculate_covering_sets_first_round>();
        
        for(j = 0; j<valid_view_mappings_per_head_var.size() + gap*i; j = j+gap*i)
        {
//          HashSet<citation_view_vector> view_com = new HashSet<citation_view_vector>();
//          
//          for(int k = j; k<j+gap*i && k < valid_view_mappings_per_head_var.size(); k++)
//          {
//            HashMap<Single_view, HashSet<Tuple>> valid_view_mappings = valid_view_mappings_per_head_var.get(k);
//            
//            Set<Single_view> views = valid_view_mappings.keySet();
//            
//            HashSet<Tuple> all_tuples = new HashSet<Tuple>();
//            
//            for(Iterator iter = views.iterator(); iter.hasNext();)
//            {
//              Single_view view = (Single_view) iter.next();
//              
//              HashSet<Tuple> tuples = valid_view_mappings.get(view);
//              
//              all_tuples.addAll(tuples);
//              
//              
//            }
//            
//            view_com = join_views_curr_relation(all_tuples, view_com, args);
//            
//          }
//          if(!view_com.isEmpty())
//            covering_sets.add(view_com);
          
          Calculate_covering_sets_first_round cal_thread = new Calculate_covering_sets_first_round(valid_view_mappings_per_head_var, args, j, j+gap*i);
          
          cal_thread.start();
              
          cal_threads.add(cal_thread);
        }
        
        for(int p = 0; p<cal_threads.size(); p++)
        {
          cal_threads.get(p).join();
        }
        
        for(int p = 0; p<cal_threads.size(); p++)
        {
          
          HashSet<citation_view_vector> view_com = cal_threads.get(p).get_reasoning_result();
          
          if(!view_com.isEmpty())
            covering_sets.add(view_com);
        }
        
        
      }
      else
      {
        int merge_times = (int) Math.ceil(valid_view_mappings_per_head_var.size()/(gap*i));
        
        ArrayList<Calculate_covering_sets> cal_threads = new ArrayList<Calculate_covering_sets>(); 
        
        
        for(int k = 0; k<covering_sets.size(); k=k+gap)
        {
          if(k + gap - 1 < covering_sets.size())
          {
            Calculate_covering_sets cal_thread = new Calculate_covering_sets(covering_sets, k, k + gap);
            
            cal_thread.start();
            
            cal_threads.add(cal_thread);
//            HashSet<citation_view_vector> updated_covering_set = join_operation();
//            
//            covering_sets.set(k/2, updated_covering_set);
          }
          else
          {
            
            Calculate_covering_sets cal_thread = new Calculate_covering_sets(covering_sets, k, covering_sets.size());
            
            cal_thread.start();
            
            cal_threads.add(cal_thread);
            
//            covering_sets.set(k/gap, covering_sets.get(k));
          }
        }
        
        for(int p = 0; p<cal_threads.size(); p++)
        {
          cal_threads.get(p).join();
        }
        
        for(int k = 0; k<covering_sets.size(); k = k + gap)
        {
          
//          if(k + gap - 1 < covering_sets.size())
          {
            HashSet<citation_view_vector> updated_covering_set = cal_threads.get(k/gap).get_reasoning_result();
            
            covering_sets.set(k/gap, updated_covering_set);
          }
          
        }
        
        
        int redundant_start = (covering_sets.size() + 1)/gap;
        
        int redundant_end = covering_sets.size();
        
        for(int k = redundant_start; k<redundant_end; k++)
        {
          covering_sets.remove(covering_sets.size() - 1);
        }
      }
    }
    
    if(loop_time == 0)
    {
      HashMap<Single_view, HashSet<Tuple>> view_mappings = valid_view_mappings_per_head_var.get(0);
      
      Set<Single_view> views = view_mappings.keySet();
      
      for(Iterator iter = views.iterator(); iter.hasNext();)
      {
        Single_view view = (Single_view) iter.next();
        
        HashSet<Tuple> tuples = view_mappings.get(view);
        
        HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
        
        for(Tuple tuple: tuples)
        {          
          if(tuple.lambda_terms.size() > 0)
          {
              
              citation_view_parametered c = new citation_view_parametered(tuple.name, tuple.query, tuple);
              
              citation_view_vector curr_views = new citation_view_vector(c);
              
              curr_covering_sets.add(curr_views);
          }   
          else
          {
              
              citation_view_unparametered c = new citation_view_unparametered(tuple.name, tuple);
              
              citation_view_vector curr_views = new citation_view_vector(c);
              
              curr_covering_sets.add(curr_views);
          }
        }
        
        return curr_covering_sets;
      }
    }
    
    return covering_sets.get(0);
  }
  
  static HashSet<citation_view_vector> reasoning_covering_set_multi_hops_conjunctive_query(ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings_per_head_var, Vector<Argument> args, HashMap<Tuple, Integer> tuple_ids, boolean multi_thread) throws InterruptedException
  {
    
    int loop_time = (int) Math.ceil(Math.log(valid_view_mappings_per_head_var.size())/Math.log(gap));
    
    ArrayList<HashSet<citation_view_vector>> covering_sets = new ArrayList<HashSet<citation_view_vector>>();
    
    for(int i = 1; i<=loop_time; i++)
    {
      int j = 0;
      
      if(i == 1)
      {
        
        for(j = 0; j<valid_view_mappings_per_head_var.size() + gap*i; j = j+gap*i)
        {
          HashSet<citation_view_vector> view_com = new HashSet<citation_view_vector>();
          
          for(int k = j; k<j+gap*i && k < valid_view_mappings_per_head_var.size(); k++)
          {
            HashMap<Single_view, HashSet<Tuple>> valid_view_mappings = valid_view_mappings_per_head_var.get(k);
            
            Set<Single_view> views = valid_view_mappings.keySet();
            
            HashSet<Tuple> all_tuples = new HashSet<Tuple>();
            
            for(Iterator iter = views.iterator(); iter.hasNext();)
            {
              Single_view view = (Single_view) iter.next();
              
              HashSet<Tuple> tuples = valid_view_mappings.get(view);
              
              all_tuples.addAll(tuples);
              
              
            }
            
            view_com = join_views_curr_relation(all_tuples, view_com, args, tuple_ids);
            
            
          }
          
          if(!view_com.isEmpty())
            covering_sets.add(view_com);
          
        }
              
      }
      else
      {
        int merge_times = (int) Math.ceil(valid_view_mappings_per_head_var.size()/(gap*i));
        
        for(int k = 0; k<covering_sets.size(); k=k+gap)
        {
          
          int start = 0;
          
          int end = 0;
          
          
          if(k + gap - 1 < covering_sets.size())
          {
            
            start = k;
            
            end = k + gap;
          }
          else
          {
            
            start = k;
            
            end = covering_sets.size();
            
          }
          
          HashSet<citation_view_vector> resulting_covering_set = covering_sets.get(start);
          
          for(int p = start + 1; p<end; p++)
          {
            resulting_covering_set = join_operation(resulting_covering_set, covering_sets.get(p));

          }
          
          covering_sets.set(k/gap, resulting_covering_set);
        }
        
        
        int redundant_start = (covering_sets.size() + 1)/gap;
        
        int redundant_end = covering_sets.size();
        
        for(int k = redundant_start; k<redundant_end; k++)
        {
          covering_sets.remove(covering_sets.size() - 1);
        }
      }
    }
    
    if(loop_time == 0)
    {
      HashMap<Single_view, HashSet<Tuple>> view_mappings = valid_view_mappings_per_head_var.get(0);
      
      Set<Single_view> views = view_mappings.keySet();
      
      for(Iterator iter = views.iterator(); iter.hasNext();)
      {
        Single_view view = (Single_view) iter.next();
        
        HashSet<Tuple> tuples = view_mappings.get(view);
        
        HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
        
        for(Tuple tuple: tuples)
        {          
          if(tuple.lambda_terms.size() > 0)
          {
              
              citation_view_parametered c = new citation_view_parametered(tuple.name, tuple.query, tuple);
              
              citation_view_vector curr_views = new citation_view_vector(c);
              
              curr_covering_sets.add(curr_views);
          }   
          else
          {
              
              citation_view_unparametered c = new citation_view_unparametered(tuple.name, tuple);
              
              citation_view_vector curr_views = new citation_view_vector(c);
              
              curr_covering_sets.add(curr_views);
          }
        }
        
        return curr_covering_sets;
      }
    }
    
    return covering_sets.get(0);
  }
  
  static HashSet<citation_view_vector> reasoning_covering_sets(double [][] distances, ArrayList<HashSet<citation_view_vector>> covering_sets_per_attributes, HashMap<Tuple, Integer> tuple_ids)
  {
//    String [] names = new String[covering_sets_per_attributes.size()];
//    
//    for(int i = 0; i < covering_sets_per_attributes.size(); i++)
//    {
//      names[i] = "att" + i;
//    }
//    
//    ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
//    Cluster cluster = alg.performClustering(distances, names,covering_sets_per_attributes,
//        new AverageLinkageStrategy());
//    
//    return cluster.merge_children();
    
    HashSet<citation_view_vector> resulting_covering_sets = new HashSet<citation_view_vector>();
    
    for(int i = 0; i<covering_sets_per_attributes.size(); i++)
    {
      if(i == 0)
        resulting_covering_sets = covering_sets_per_attributes.get(i);
      else
        resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(i));
    }
    
//    DataProvider provider = new MatrixProvider(distances);
//    
//    AproBuilder builder = new AproBuilder();
//    builder.setThreads(1); // no parallelization
//    Apro apro = builder.build(provider);
//    
//    apro.setDebug(false);
//    
//    apro.run(200);
//    
//    HashMap<Integer, HashSet<citation_view_vector>> covering_sets_per_cluster = new HashMap<Integer, HashSet<citation_view_vector>>();
//    
//    System.out.println(apro.getExemplarSet().size());
//    
//    double start = System.nanoTime();
//
//    
//    for(int i = 0; i<apro.getExemplars().length; i++)
//    {
//      int exemplar = apro.getExemplars()[i];
//            
//      if(covering_sets_per_cluster.get(exemplar) == null)
//      {
//        covering_sets_per_cluster.put(exemplar, covering_sets_per_attributes.get(i));
//      }
//      else
//      {
//        HashSet<citation_view_vector> resulting_covering_sets = covering_sets_per_cluster.get(exemplar);
//        
//        resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(i));
//        
//        covering_sets_per_cluster.put(exemplar, resulting_covering_sets);
//      }
//      
//    }
//    
//    for(int i = 0; i<apro.getExemplars().length; i++)
//    {
//      int exemplar = apro.getExemplars()[i];
//            
//      if(covering_sets_per_cluster.get(exemplar) == null)
//      {
//        covering_sets_per_cluster.put(exemplar, covering_sets_per_attributes.get(i));
//      }
//      else
//      {
//        HashSet<citation_view_vector> resulting_covering_sets = covering_sets_per_cluster.get(exemplar);
//        
//        resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(i));
//        
//        covering_sets_per_cluster.put(exemplar, resulting_covering_sets);
//      }
//      
//    }
//    
//    double end_time = System.nanoTime();
//    
//    double time = (end_time - start)/1000000000;
//    
//    System.out.println(time);
//    
//    HashSet<citation_view_vector> resulting_covering_sets = null;
//    
//    Set<Integer> ids = covering_sets_per_cluster.keySet();
//    
//    for(Integer id : ids)
//    {
//      HashSet<citation_view_vector> curr_covering_sets = covering_sets_per_cluster.get(id);
//      
//      if(resulting_covering_sets == null)
//      {
//        resulting_covering_sets = curr_covering_sets;
//      }
//      else
//      {
//        resulting_covering_sets = join_operation(resulting_covering_sets, curr_covering_sets);
//      }
//    }
    
    return resulting_covering_sets;
    
  }
  
  
  static int get_next_cluster(double [][]distances, int curr_id, HashMap<Integer, Vector<Integer>> clusters, Vector<Integer> sorted_ids, Set<Integer> all_ids)
  {
    Vector<Integer> curr_cluster = clusters.get(curr_id);
    
    double min_max_cluster_distance = Double.MAX_VALUE;
    
    int min_max_cluster_id = -1;
    
    for(Integer id: all_ids)
    {
      if(!sorted_ids.contains(id) && id != curr_id)
      {
        Vector<Integer> curr_check_cluster = clusters.get(id);
        
        double max_distance = -1;
        
        for(int i = 0; i<curr_cluster.size(); i++)
        {
          for(int j = 0; j<curr_check_cluster.size(); j++)
          {
            if(distances[curr_cluster.get(i)][curr_check_cluster.get(j)] * (-1) > max_distance)
            {
              max_distance = distances[curr_cluster.get(i)][curr_check_cluster.get(j)] * (-1); 
            }
          }
        }
        
        if(max_distance < min_max_cluster_distance)
        {
          min_max_cluster_distance = max_distance;
          
          min_max_cluster_id = id;
        }
        
      }
    }
    
    return min_max_cluster_id;
  }
  
  static HashSet<citation_view_vector> reasoning_covering_set_ap(double [][] distances, ArrayList<HashSet<citation_view_vector>> covering_sets_per_attributes, HashMap<Tuple, Integer> tuple_ids)
  {
//    String [] names = new String[covering_sets_per_attributes.size()];
//    
//    for(int i = 0; i < covering_sets_per_attributes.size(); i++)
//    {
//      names[i] = "att" + i;
//    }
//    
//    ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
//    Cluster cluster = alg.performClustering(distances, names,covering_sets_per_attributes,
//        new AverageLinkageStrategy());
//    
//    return cluster.merge_children();
    
    DataProvider provider = new MatrixProvider(distances);
    
    AproBuilder builder = new AproBuilder();
    builder.setDebug(false);
    
    builder.setThreads(1); // no parallelization
    Apro apro = builder.build(provider);
    apro.setDebug(false);
    
    apro.run(200);
    
//    HashMap<Integer, HashSet<citation_view_vector>> covering_sets_per_cluster = new HashMap<Integer, HashSet<citation_view_vector>>();
    
//    System.out.println(apro.getExemplarSet().size());
    
    double start = System.nanoTime();

    
    HashMap<Integer, Vector<Integer>> clusters = new HashMap<Integer, Vector<Integer>>();
    
    for(int i = 0; i<apro.getExemplars().length; i++)
    {
      int exemplar = apro.getExemplars()[i];
      
      if(clusters.get(exemplar) == null)
      {
        Vector<Integer> points = new Vector<Integer>();
        
        points.add(i);
        
        clusters.put(exemplar, points);
      }
      else
      {
        clusters.get(exemplar).add(i);
      }
    }


    double end_time = System.nanoTime();
    
    double time = (end_time - start)/1000000000;
    
    HashSet<citation_view_vector> resulting_covering_sets = null;
    
    Set<Integer> ids = clusters.keySet();
    
    if(sort_cluster)
    {
      Vector<Integer> sorted_ids = new Vector<Integer>();
      
      Integer curr_id = 0;
      
      for(Integer id:ids)
      {
        if(sorted_ids.isEmpty())
        {
          curr_id = id;
          
          sorted_ids.add(id);
        }
        else
        {
          curr_id = get_next_cluster(distances, curr_id, clusters, sorted_ids, ids);
          
          sorted_ids.add(curr_id);
        }
      }
      
      
      
      
      for(Integer id : sorted_ids)
      {
        Vector<Integer> points = clusters.get(id);
        
//        HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
        
        for(int i = 0; i<points.size(); i++)
        {
          if(resulting_covering_sets == null)
          {
            resulting_covering_sets = covering_sets_per_attributes.get(points.get(i));
          }
          else
          {
            resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(points.get(i)));
          }
        }
        
        
//        if(resulting_covering_sets == null)
//        {
//          resulting_covering_sets = curr_covering_sets;
//        }
//        else
//        {
//          resulting_covering_sets = join_operation(resulting_covering_sets, curr_covering_sets);
//        }
      }
    }
    else
    {
      for(Integer id : ids)
      {
        Vector<Integer> points = clusters.get(id);
        
//        HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
        
        for(int i = 0; i<points.size(); i++)
        {
          if(resulting_covering_sets == null)
          {
            resulting_covering_sets = covering_sets_per_attributes.get(points.get(i));
          }
          else
          {
            resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(points.get(i)));
          }
        }
        
        
//        if(resulting_covering_sets == null)
//        {
//          resulting_covering_sets = curr_covering_sets;
//        }
//        else
//        {
//          resulting_covering_sets = join_operation(resulting_covering_sets, curr_covering_sets);
//        }
      }
    }
    
    
//    Vector<Integer> sorted_ids = new Vector<Integer>();
//    
//    Integer curr_id = 0;
//    
//    for(Integer id:ids)
//    {
//      if(sorted_ids.isEmpty())
//      {
//        curr_id = id;
//        
//        sorted_ids.add(id);
//      }
//      else
//      {
//        curr_id = get_next_cluster(distances, curr_id, clusters, sorted_ids, ids);
//        
//        sorted_ids.add(curr_id);
//      }
//    }
//    
//    
//    
//    
//    for(Integer id : sorted_ids)
//    {
//      Vector<Integer> points = clusters.get(id);
//      
////      HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
//      
//      for(int i = 0; i<points.size(); i++)
//      {
//        if(resulting_covering_sets == null)
//        {
//          resulting_covering_sets = covering_sets_per_attributes.get(points.get(i));
//        }
//        else
//        {
//          resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(points.get(i)));
//        }
//      }
//      
//      
////      if(resulting_covering_sets == null)
////      {
////        resulting_covering_sets = curr_covering_sets;
////      }
////      else
////      {
////        resulting_covering_sets = join_operation(resulting_covering_sets, curr_covering_sets);
////      }
//    }
//    for(int i = 0; i<apro.getExemplars().length; i++)
//    {
//      int exemplar = apro.getExemplars()[i];
//            
//      if(covering_sets_per_cluster.get(exemplar) == null)
//      {
//        covering_sets_per_cluster.put(exemplar, covering_sets_per_attributes.get(i));
//      }
//      else
//      {
//        HashSet<citation_view_vector> resulting_covering_sets = covering_sets_per_cluster.get(exemplar);
//        
//        resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(i));
//        
//        covering_sets_per_cluster.put(exemplar, resulting_covering_sets);
//      }
//      
//    }
    
//    for(int i = 0; i<apro.getExemplars().length; i++)
//    {
//      int exemplar = apro.getExemplars()[i];
//            
//      if(covering_sets_per_cluster.get(exemplar) == null)
//      {
//        covering_sets_per_cluster.put(exemplar, covering_sets_per_attributes.get(i));
//      }
//      else
//      {
//        HashSet<citation_view_vector> resulting_covering_sets = covering_sets_per_cluster.get(exemplar);
//        
//        resulting_covering_sets = join_operation(resulting_covering_sets, covering_sets_per_attributes.get(i));
//        
//        covering_sets_per_cluster.put(exemplar, resulting_covering_sets);
//      }
//      
//    }
    
    
    return resulting_covering_sets;
    
  }
  
  static HashSet<citation_view_vector> reasoning_covering_set_conjunctive_query(ArrayList<HashMap<Single_view, HashSet<Tuple>>> valid_view_mappings_per_head_var, Vector<Argument> args, HashMap<Tuple, Integer> tuple_ids, boolean multi_thread) throws InterruptedException
  {
    
    int loop_time = (int) Math.ceil(Math.log(valid_view_mappings_per_head_var.size())/Math.log(2));
    
    ArrayList<HashSet<citation_view_vector>> covering_sets = new ArrayList<HashSet<citation_view_vector>>();
    
    for(int i = 1; i<=loop_time; i++)
    {
      int j = 0;
      
      if(i == 1)
      {
        
        for(j = 0; j<valid_view_mappings_per_head_var.size() + 2*i; j = j+2*i)
        {
          HashSet<citation_view_vector> view_com = new HashSet<citation_view_vector>();
          
          for(int k = j; k<j+2*i && k < valid_view_mappings_per_head_var.size(); k++)
          {
            HashMap<Single_view, HashSet<Tuple>> valid_view_mappings = valid_view_mappings_per_head_var.get(k);
            
            Set<Single_view> views = valid_view_mappings.keySet();
            
            HashSet<Tuple> all_tuples = new HashSet<Tuple>();
            
            for(Iterator iter = views.iterator(); iter.hasNext();)
            {
              Single_view view = (Single_view) iter.next();
              
              HashSet<Tuple> tuples = valid_view_mappings.get(view);
              
              all_tuples.addAll(tuples);
              
              
            }
            
            view_com = join_views_curr_relation(all_tuples, view_com, args, tuple_ids);
            
          }
          
          if(!view_com.isEmpty())
             covering_sets.add(view_com);
        }
        
        
      }
      else
      {
        int merge_times = (int) Math.ceil(valid_view_mappings_per_head_var.size()/(2*i));
        
        ArrayList<Calculate_covering_sets> cal_threads = new ArrayList<Calculate_covering_sets>(); 
        
        
        for(int k = 0; k<covering_sets.size(); k=k+2)
        {
          if(k + 2 - 1 < covering_sets.size())
          {
            Calculate_covering_sets cal_thread = new Calculate_covering_sets(covering_sets, k, k + 2);
            
            cal_thread.start();
            
            cal_threads.add(cal_thread);
//            HashSet<citation_view_vector> updated_covering_set = join_operation();
//            
//            covering_sets.set(k/2, updated_covering_set);
          }
          else
          {
            covering_sets.set(k/2, covering_sets.get(k));
          }
        }
        
        for(int p = 0; p<cal_threads.size(); p++)
        {
          cal_threads.get(p).join();
        }
        
        for(int k = 0; k<covering_sets.size(); k = k + 2)
        {
          
          if(k + 2 - 1 < covering_sets.size())
          {
            HashSet<citation_view_vector> updated_covering_set = cal_threads.get(k/2).get_reasoning_result();
            
            covering_sets.set(k/2, updated_covering_set);
          }
          
        }
        
        
        int redundant_start = (covering_sets.size() + 1)/2;
        
        int redundant_end = covering_sets.size();
        
        for(int k = redundant_start; k<redundant_end; k++)
        {
          covering_sets.remove(covering_sets.size() - 1);
        }
      }
    }
    
    return covering_sets.get(0);
  }
  
  public static HashSet<citation_view_vector> join_operation(HashSet<citation_view_vector> c_combinations, HashSet<citation_view_vector> insert_citations)
  {
/*      if(i == 0)
      {

          c_combinations.addAll(insert_citations);
          
          return c_combinations;
      }
      else*/
      {
          
          HashSet<citation_view_vector> updated_c_combinations = new HashSet<citation_view_vector>();
          
          for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
          {
              
              citation_view_vector curr_combination1 = (citation_view_vector) iter.next();
              
              HashSet<citation_view_vector> iterated_covering_sets = new HashSet<citation_view_vector>();
                              
              for(Iterator it = insert_citations.iterator(); it.hasNext();)
              {
                  
                  citation_view_vector curr_combination2 = (citation_view_vector)it.next(); 
                  
                  iterated_covering_sets.add(curr_combination2);
                  
                  citation_view_vector new_citation_vec = curr_combination2.clone();
                  
//                  if(new_citation_vec.c_vec.size() == 1)
//                  {
//                    for(citation_view c :new_citation_vec.c_vec)
//                    {
//                      if(c.get_name().equals("v8"))
//                      {
//                        System.out.println(c.get_table_name_string() + "::" + c.get_view_tuple());
//                      }
//                    }
//                  }
                  
                  citation_view_vector new_covering_set = curr_combination1.merge(new_citation_vec);
                  
//                  if(curr_combination1.toString().equals("v14*v15*v29*v4"))
//                  {
//                    System.out.println("merge_covering_sets::" + new_covering_set.toString() + "::tables::" + new_covering_set.table_names + "::attributes::" + new_covering_set.head_variables);
//                  }
//                  
//                  if(new_covering_set.toString().equals("v14*v15*v29*v4*v8"))
//                  {
//                    System.out.println("merge_covering_sets::" + new_covering_set.toString() + "::tables::" + new_covering_set.table_names + "::attributes::" + new_covering_set.head_variables);
//                  }
                  
                  remove_duplicate(updated_c_combinations, new_covering_set);
              }
          }
          
          return updated_c_combinations;
          
      }
  }
  
  public static HashSet<citation_view_vector> join_views_curr_relation(HashSet<Tuple> tuples, HashSet<citation_view_vector> curr_view_com, Vector<Argument> args, HashMap<Tuple, Integer> tuple_ids)
  {
      if(curr_view_com.isEmpty())
      {
          if(tuples.isEmpty())
              return new HashSet<citation_view_vector>();
          else
          {
              HashSet<citation_view_vector> new_view_com = new HashSet<citation_view_vector>();
              
              for(Tuple tuple:tuples)
              {
                  
                  Tuple valid_tuple = (Tuple) tuple.clone();
                  
                  valid_tuple.args.retainAll(args);
                                    
                  if(valid_tuple.lambda_terms.size() > 0)
                  {
                      
                      citation_view_parametered c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple, query_subgoal_id_mappings, query_arg_id_mappings, tuple_ids);
                      
                      citation_view_vector curr_views = new citation_view_vector(c);
                      
                      remove_duplicate(new_view_com, curr_views);
                  }   
                  else
                  {
                      
                      citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple, query_subgoal_id_mappings, query_arg_id_mappings, tuple_ids);
                      
                      citation_view_vector curr_views = new citation_view_vector(c);
                      
                      remove_duplicate(new_view_com, curr_views);
                  }
              }
              
              return new_view_com;
          }
      }
      
      else
      {
          HashSet<citation_view_vector> new_view_com = new HashSet<citation_view_vector>();
          
          for(Tuple tuple:tuples)
          {
              Tuple valid_tuple = (Tuple)tuple.clone();
              
              valid_tuple.args.retainAll(args);
              
              citation_view c = null;
              
              if(valid_tuple.lambda_terms.size() > 0)
              {
                  
                  c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple);
              }   
              else
              {
                  
                  c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
              }
              
              for(Iterator iter = curr_view_com.iterator(); iter.hasNext();)
              {
                  citation_view_vector old_view_com = (citation_view_vector)iter.next();
                  
                  citation_view_vector old_view_com_copy = old_view_com.clone(); 
                  
                  citation_view_vector view_com = citation_view_vector.merge(old_view_com_copy, c);
                  
//                  HashSet<String> string_list = new HashSet<String>();
//                  
//                  for(int j = 0; j<view_com.c_vec.size(); j++)
//                  {
//                      string_list.add(view_com.c_vec.get(j).get_name());
//                  }
//                  
//                if(string_list.contains("v4") && string_list.contains("v8") && string_list.contains("v11") && string_list.contains("v6") && string_list.contains("v14") && string_list.contains("v20"))
//                {
//                    int y = 0;
//                    
//                    y++;
//                }
//                if(string_list.contains("v4") && string_list.contains("v8"))
//                {
//                    int y = 0;
//                    
//                    y++;
//                }
                  
                  remove_duplicate(new_view_com, view_com);
              }
          }
          
          return new_view_com;
      }
  }
  
//  public static HashSet<citation_view_vector> remove_duplicate_arg(HashSet<citation_view_vector> c_combinations, citation_view_vector c_view)
//  {
//      int i = 0;
//      
//      if(c_combinations.contains(c_view))
//          return c_combinations;
//              
//      for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
//      {
////        String str = (String) iter.next();
//                      
//          citation_view_vector c_combination = (citation_view_vector) iter.next();
//          {
//              {
//                  citation_view_vector curr_combination = c_view;
//                  if(view_vector_contains(c_combination, curr_combination)&& curr_combination.head_variables.containsAll(c_combination.head_variables) && c_combination.index_vec.size() > curr_combination.index_vec.size())
//                  {
//                      iter.remove();                      
//                  }
//                  
//                  if(view_vector_contains(curr_combination, c_combination) && c_combination.head_variables.containsAll(curr_combination.head_variables) && curr_combination.index_vec.size() > c_combination.index_vec.size())
//                  {
//                      break;
//                  }
//              }
//              
//          }
//          
//          i++;
//      }
//      
//      
//      if(i >= c_combinations.size())
//          c_combinations.add(c_view);
//      
//              
//      return c_combinations;
//  }
  
  public static HashSet<citation_view_vector> remove_duplicate(HashSet<citation_view_vector> c_combinations, citation_view_vector c_view)
  {
      int i = 0;
      
      if(c_combinations.contains(c_view))
      {
        return c_combinations;
      }
                    
      boolean removed = false;
      
      for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
      {
//        String str = (String) iter.next();
                      
          citation_view_vector c_combination = (citation_view_vector) iter.next();
          
//        if(c_combination.toString().equals("v11*v20*v4*v8"))
//        {
//            int y = 0;
//            
//            y++;
//        }
          {
              {
                  citation_view_vector curr_combination = c_view;
                  
                  if(citation_view_vector.contains(c_combination.tuple_index, curr_combination.tuple_index) && citation_view_vector.contains(curr_combination.arg_name_index,  c_combination.arg_name_index) && citation_view_vector.contains(curr_combination.table_name_index, c_combination.table_name_index))
                  {
                      iter.remove();   
                      
                      
                      
                  }
                  
//                  if(curr_combination.c_vec.containsAll(c_combination.c_vec) && c_combination.head_variables.containsAll(curr_combination.head_variables) && curr_combination.index_vec.size() > c_combination.index_vec.size())
                  if(citation_view_vector.contains(curr_combination.tuple_index, c_combination.tuple_index) && citation_view_vector.contains(c_combination.arg_name_index,  curr_combination.arg_name_index) && citation_view_vector.contains(c_combination.table_name_index, curr_combination.table_name_index))
                  {
                    
                    removed = true;
                    
                      break;
                  }
              }
              
          }
          
          i++;
      }
      
      if(!removed)
      {
        c_combinations.add(c_view);
        
      }
              
      return c_combinations;
  }
  
  static boolean view_vector_contains(citation_view_vector c_vec1, citation_view_vector c_vec2)
  {
      
      String s1 = ".*?";
      
      String s2 = c_vec1.index_str;
      
      for(int i = 0; i<c_vec2.index_vec.size(); i++)
      {
          String str = c_vec2.index_vec.get(i);
          
          str = str.replaceAll("\\(", "\\\\(");
          
          str = str.replaceAll("\\)", "\\\\)");
          
          str = str.replaceAll("\\[", "\\\\[");
          
          str = str.replaceAll("\\]", "\\\\]");
          
          str = str.replaceAll("\\/", "\\\\/");
          
          s1 += "\\(" + str + "\\).*?";
      }
      
      return s2.matches(s1);

  }
  
  
  
//    Set<Single_view> views = possible_valid_view_mappings.keySet();
//    
//    for(Iterator iter = views.iterator(); iter.hasNext(); )
//    {
//      Single_view view = (Single_view) iter.next();
//      
//      HashSet<Tuple> tuples1 = possible_valid_view_mappings.get(view);
//      
//      HashSet<Tuple> tuples2 = all_possible_view_mappings.get(view);
//      
//      tuples1.retainAll(tuples2);
//      
//      view.check_where_why_provenance_tokens(tuples1, where_token, where_why_tokens);
//            
//      if(tuples1.isEmpty())
//      {
//        iter.remove();
//      }
//    }
  
  static HashMap<Single_view, HashSet<Tuple>> get_all_possible_view_mappings(HashMap<String, Integer> subgoal_id_mappings, Query q)
  {
    Database canDb = CoreCover.constructCanonicalDB(q.body, q.subgoal_name_mapping);
    
    HashMap<Single_view, HashSet<Tuple>> view_mappings = new HashMap<Single_view, HashSet<Tuple>>();
    
    for(int i = 0; i<view_objs.size(); i++)
    {
      Single_view view = view_objs.get(i);
      
//      System.out.println(view.subgoal_name_id_mappings);
      
      view.build_view_mappings(q.body, canDb, subgoal_id_mappings);
      
      if(!view.view_mappings.isEmpty())
        view_mappings.put(view, view.view_mappings);
      
    }
    
    return view_mappings;
  }
  
}