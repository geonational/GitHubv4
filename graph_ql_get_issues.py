#!/usr/bin/env python3
import requests

# Using GraphQL in GitHub v4  run a graphQL query passing variables
# query data for Open GitHub Issues in repo and the last comment
# The data is returned as JSON into the result variable
# and then extracted into Python variables.


gh_owner = "geonational"
gh_repos = "GitHubv4"
gh_assignee = "geonational"
gh_label = "jenkinsdeployment"



# Get your personal token from your github profile - developer tools
headers = {"Authorization": "Bearer xxxxxxxxxxxxxxxxxxxxxxxxx"}


def run_query(query, variables): # A simple function to use requests.post to make the API call. Note the json= section.
	request = requests.post('https://api.github.com/graphql', json={'query': query, 'variables': variables}, headers=headers)
	if request.status_code == 200:
		return request.json()
	else:
		raise Exception("Query failed to run by returning code of {}. {}".format(request.status_code, query))
	

#Build this in the GitHub Explorer
#https://developer.github.com/v4/explorer/
#The first line defines the GraphQL variables 

query = '''query($owner: String!, $repos_name: String!,$assignee: String!,$label: String!) {
			repository(owner: $owner, name: $repos_name) {
			issues(last: 5, states: OPEN, filterBy: {assignee: $assignee, labels: $label}) {
			edges {
				node {
							title
							url
							bodyHTML
					assignees(last: 5) {
						edges {
							node {
								login
								}
							}
						}
					comments(last: 1) {
						edges {
						node {
							bodyHTML
							}
						}
						}
					}
					}
					}
			}
			}
			'''


#Setting the python varibles to GraphQL variables 
variables = {
	"owner": gh_owner,
	"repos_name": gh_repos,
	"assignee" : gh_assignee,
	"label" : gh_label
	}	

	

try:

    result = run_query(query, variables)  # execute query

   
    # You may need to find the type of each of the layers to know how to query it
    # json_rows = result['data'],['repository'],['issues'],['edges'],['node'],['url']
    # json_rows = result['data'],['repository'],['issues'] -- tuple
    # json_rows = result['data'],['repository'] -- tuple
    # json_rows = result['data'] -- dict
    # print(type(json_rows))

    # These dicts and tuples so do not need a loop

    all_data = result['data']['repository']['issues']['edges']

    # You need a loop to go though a list

    for issue_node_data in all_data:
        issue_node_access = issue_node_data['node']

        # now we are in a dict again so direct access

        title_data = issue_node_access['title']
        url_data = issue_node_access['url']
        html_issue_data = issue_node_access['bodyHTML']
        print (title_data)
        print (url_data)
        print (html_issue_data)

        # Moving down these are two are dicts so no loop needed

        comment_node_traverse = issue_node_access['comments']['edges']

        # print(comment_node_traverse)

        # Node so generate a list

        for comment_node_data in comment_node_traverse:
            comment_node_access = comment_node_data['node']
            html_comment_data = comment_node_access['bodyHTML']
            print (html_comment_data)
			
	# Dump all the JSON
    # print(result)


except Exception as e:
    print (e.args[0])