package edu.upenn.cis.citation.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.init.init;
import edu.upenn.cis.citation.pre_processing.view_operation;

public class Load_views_and_citation_queries {

	
	static String split1 = "|";
	
	static int col_nums = 4;
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(init.db_url,
       	        init.usr_name,init.passwd);
        
        PreparedStatement pst = null;
		
		Vector<Query> queries = get_views("data/Exp1/views", c, pst);
		
		for(int i = 0; i < queries.size(); i++)
		{
			System.out.println(queries.get(i));
		}
		
		c.close();
	}
	
	public static Vector<Query> get_views(String file, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Query> queries = new Vector<Query>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	
		    	queries.add(get_view(line, c, pst));
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return queries;
	}
	
	static Query get_view(String line, Connection c, PreparedStatement pst) throws SQLException
	{
//		System.out.println(line);
		
		String [] strs = line.split("\\" + split1);
		
		String view_name = strs[0];
		
		String heads = strs[1];
		
		String body = strs[2];
		
		String predicates = strs[3];
		
		String lambda_term_str = strs[4];
		
		String relation_mapping_str = strs[5];
		
		HashMap<String, Argument> name_arg_mappings = new HashMap<String, Argument>();
		
		HashMap<String, String> relation_mapping = get_relation_mapping(relation_mapping_str);
		
		Vector<Subgoal> relational_subgoals = split_bodies(body, relation_mapping, name_arg_mappings, c ,pst);
		
		Subgoal head_subgoal = new Subgoal(view_name, split_head(heads, name_arg_mappings));
        		
		Vector<Conditions> predicate_subgoal = split_predicates(predicates, name_arg_mappings);
		
		Vector<Lambda_term> l_terms = split_lambda_terms(lambda_term_str);
		
		return new Query(view_name, head_subgoal, relational_subgoals, l_terms, predicate_subgoal, relation_mapping);
		
	}
	
	static Vector<Lambda_term> split_lambda_terms(String lambda_term_str)
	{
		
		if(lambda_term_str.isEmpty())
		{
			return new Vector<Lambda_term>();
		}
		
		String [] lambda_strs = lambda_term_str.split(",");
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<lambda_strs.length; i++)
		{
			String [] relation_arg = lambda_strs[i].trim().split("\\" + ".");
			
			
			l_terms.add(new Lambda_term(relation_arg[0].trim() + init.separator + relation_arg[1].trim(), relation_arg[0].trim()));
		}
		
		return l_terms;
	}
	
	static Vector<Conditions> split_predicates(String predicates_str, HashMap<String, Argument> name_arg_mappings)
	{
		if(predicates_str.isEmpty())
			return new Vector<Conditions>();
		
		String [] predicates = predicates_str.split(",");
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		for(int i = 0; i<predicates.length; i++)
		{
			String predicate = predicates[i].trim();
						
			Conditions condition = view_operation.parse_conditions(predicate, name_arg_mappings, ".");
			
			conditions.add(condition);
		}
		
		return conditions;
	}
	
	static HashMap<String, String> get_relation_mapping(String relation_mapping_str)
	{
		String [] relation_mappings = relation_mapping_str.split(",");
		
		HashMap<String, String> relation_mapping = new HashMap<String, String>();
		
		for(int i = 0; i<relation_mappings.length; i++)
		{
			String [] curr_relation_mapping = relation_mappings[i].trim().split(":");
			
			relation_mapping.put(curr_relation_mapping[0].trim(), curr_relation_mapping[1].trim());
			
		}
		
		return relation_mapping;
	}
	
	
	
	static Vector<Subgoal> split_bodies(String body_str, HashMap<String, String> relation_mapping, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
	{
		String [] relations = body_str.split(",");
		
        Vector<Subgoal> subgoals = new Vector<Subgoal>();
        
		for(int i = 0; i<relations.length; i++)
		{
			String relation = relations[i].trim();
			
			Vector<Argument> args = view_operation.get_full_schema(relation, relation_mapping.get(relation), name_arg_mappings, c, pst);
			
			Subgoal subgoal = new Subgoal(relation, args);
			
			subgoals.add(subgoal);
		}
				
		return subgoals;
	}
	
	static Vector<Argument> split_head(String head, HashMap<String, Argument> name_arg_mappings)
	{		
		String [] head_arg_strs = head.split(",");
		
		Vector<Argument> head_args = new Vector<Argument>();
		
		for(int i = 0; i<head_arg_strs.length; i++)
		{
			
			String [] relation_arg = head_arg_strs[i].trim().split("\\" + ".");
			
			String relation = relation_arg[0].trim();
			
			String arg = relation_arg[1].trim();
			
			Argument Arg = name_arg_mappings.get(relation + init.separator + arg);
			
			head_args.add(Arg);
		}
		
		return head_args;
	}
	
	
	
}