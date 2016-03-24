#!/usr/bin/python
# -*- coding: utf-8 -*-

import gitlab
import ConfigParser
import sys

class ChangelogGenerator:
    """
    Generates a changelog in Markdown depending on :
    - The Tags of the git repository 
    - The settled milestones in the gitlab 
    This script uses Python-GitLab: https://github.com/Itxaka/pyapi-gitlab

    /!\ To get this script working, you should:
        - Name your tags the same way you set the title of a milestone. (e.g. v1.0.0)
            These names will represent the different versions of the project
        - Create a property 'gitlab_token' in a 'local.properties' file in your working directory 
            This property must contain your gitlab API Key
    """

    FEATURE_KEYWORD = "Feature"
    FEATURE_TAGS = ["feature", "enhancement"]

    BUG_KEYWORD = "Bug"
    BUG_TAGS = ["bug"]

    MISC_KEYWORD = "Misc."
    MISC_TAGS = ["documentation", "discussion"]

    STATE_CLOSED = "closed"
    STATE_OPEN = "opened"

    API_TOKEN_FILE = "local.properties"
    API_TOKEN_KW = "gitlab_token"

    GITLAB_URL = "http://code.apisense.com"

    def __init__(self, project):
        """
        Create a new instance of changelogGenerator.
        Param:
            - version : String or Int
                This argument might either be the name or the id of the project on GitLab.
        """
        self.password = self._get_api_token()
        self.git = gitlab.Gitlab(self.GITLAB_URL, token=self.password)
        if (type(project) == str):
            self.project_id = self._get_project_id(project)
        else:
            if (type(project) == int):
                self.project_id = project
            else:
                raise TypeError("project argument must either be a string (name) or an int (id)")
        if not self.git.getproject(self.project_id):
            raise Exception("Project '"+ project +"' not found in Gitlab (" + self.GITLAB_URL + ")")

    def _get_api_token(self):
        config = ConfigParser.RawConfigParser()
        config.readfp(FakeSecHead(open(self.API_TOKEN_FILE)))
        password = config.get(FakeSecHead.FAKE_SECTION, self.API_TOKEN_KW)
        return password

    def _get_tags(self):
        """
        Returns the names of every tags of the project,
        which should represent a project version.
        Return: List<String>
            The existing tags name on the GitLab project.
        """
        tags = self.git.listrepositorytags(self.project_id)
        return [tag["name"] for tag in tags];
        
    def _get_milestones(self):
        """
        Returns the title of every milestones of the project, 
        which should represent a project version.
        Return: List<String>
            The existing milestones title on the GitLab project.
        """       
        mstones = self.git.getmilestones(self.project_id)
        return [mstone["title"] for mstone in mstones]
        

    def _get_project_id(self, projectName):
        """
        Retrieve the id of the project corresponding the given name.
        Param: 
            - projectName : String
                The name of the project to retrieve on GitLab.
        Return: Integer
            The id of the named project
        """
        projects = self.git.getprojects()
        for project in projects:
            if (project["name"] == projectName):
                return project["id"]
        return None;

    def _sort_issues(self, issues):
        """
        Generate a Dictionnary containing 2 Dictionnary of issues: open and closed.
        Each Dictionnary contains a 3 lists of issues: Feature, Bug, Misc.
        Param:
            - issues : List<Issues>
                The fetched list of issues attached to the project.
        Return: Dictionnary<String, Dictionnary<String, List>>
            Sorted issues by Opening status, then by category.
        """
        sorted_issues = {
            self.STATE_CLOSED: {
                self.FEATURE_KEYWORD : [],
                self.BUG_KEYWORD : [],
                self.MISC_KEYWORD : []
            }, 
            self.STATE_OPEN: {
                self.FEATURE_KEYWORD : [],
                self.BUG_KEYWORD : [],
                self.MISC_KEYWORD : []
            }
        }
        for issue in issues: 
            tags = issue['labels']
            category = self.MISC_KEYWORD
            # Currently use last tag to infer category
            for tag in tags:
                if tag in self.FEATURE_TAGS:
                    category = self.FEATURE_KEYWORD 
                if tag in self.BUG_TAGS:
                    category = self.BUG_KEYWORD
                if tag in self.MISC_TAGS:
                    category = self.MISC_KEYWORD
            sorted_issues[issue["state"]][category].append(issue)
        return sorted_issues

    def _get_issues_for_version(self, version):
        """
        Return a list of the issues set to the milestone indicating the given version
        Param:
            - version : String
                Identifier of the version to use.
        Return: List<Issue>
            The list of issues settled to given version.
        """
        issues = self.git.getprojectissues(self.project_id)
        concerned_issues = []
        for issue in issues:
            if (issue["milestone"]["title"] == version):
                concerned_issues.append(issue)
        return concerned_issues

    def _generate_md_categorized_issues_list(self, issues):
        """
        Generate a Categorized list of issues in Markdown from a Dictionnary (Category, issues).
        Param:
            - issues - Dictionnary<String, List>
                Lists of issues sorted by categories.
        Return: String
            Markdown representation of the issues list.
        """
        markdown = ""
        for elementType, elements in issues:
            if elements:
                markdown += "- " + elementType + "\n"
                for issue in elements:
                    markdown += "    - " + issue["title"] + "\n"
        return markdown

    def generate_specific_version_changelog(self, version, showNotClosed=True):
        """
        Generate and returns Markdown representation of the modifications for the given version.
        Param:
            - version : String
                Identifier of the version to use.
            - showNotClosed: boolean (Default=True)
                Tell to show or not unclosed issues of a milestone.
        Return: String
            Markdown representation of the given version.
        """
        issues = self._sort_issues(self._get_issues_for_version(version))
        markdown = ""
        # If new elements, Add 'Newly integrated' section
        list_md = self._generate_md_categorized_issues_list(issues[self.STATE_CLOSED].items())
        if list_md:
            markdown += "## Newly integrated:\n"
            markdown += list_md

        # If delayed elements, and asked to show them, Add 'Delayed' section
        list_md = ""
        if showNotClosed:
            list_md = self._generate_md_categorized_issues_list(issues[self.STATE_OPEN].items())
            if list_md:
                markdown += "## To be reported to another version:\n"
                markdown += list_md

        # If changes occurs in a section, add section title on top of the markdown
        if markdown:
            markdown = "# Version __" + version + "__:\n" + markdown + "\n"

        return markdown

    def generate_last_version_changelog(self, showNotClosed=True):
        """
        Generate and return Markdown for last Tagged version only.
        Param: 
            - showNotClosed: boolean (Default:True)
                Tell to show or not unclosed issues of a milestone.
        Return: String
            Markdown representation of the last known version.
        """
        # Assuming versions are in chronological order (tocheck)
        last_version = self._get_tags()[0]
        return self.generate_specific_version_changelog(last_version, showNotClosed)
        
    def generate_overall_changelog(self, showNotClosed=True):
        """
        Generate and return Markdown for every tagged versions.
        Param: 
            - showNotClosed: boolean (Default:True)
                Tell to show or not unclosed issues of a milestone.
        Return: String
            Markdown representation of the project changes.
        """        
        versions = self._get_tags()
        markdown = ""
        for version in versions:
            markdown += self.generate_specific_version_changelog(version, showNotClosed)
        return markdown

class ChangelogWriter:
    """
    Write down generated String into the project Changelog file.
    """
    CHANGELOG_FILE = "CHANGELOG.md"

    def prepend_version(self, changelog):
        """
        Write the given string at the TOP of the changelog file.
        Param:
            - changelog : String
                String representation of the changelog to write down
        """
        with open(self.CHANGELOG_FILE, "r+") as file:
            prev_content = file.read()
            file.seek(0)
            file.write(changelog + prev_content)

    def append_version(self, changelog):
        """
        Write the given string at the END of the changelog file.
        Param:
            - changelog : String
                String representation of the changelog to write down
        """
        with open(self.CHANGELOG_FILE, "a") as file:
            file.write(changelog)

    def write_new_changelog(self, changelog):
        """
        Write the given string, TRUNCATING the entire changelog file.
        Param:
            - changelog : String
                String representation of the changelog to write down
        """
        with open(self.CHANGELOG_FILE, "w") as file:
            file.write(changelog)

class FakeSecHead(object):
    """
    Trick the ConfigParser python module to simulate a section at the beginning of the properties file.
    See: http://stackoverflow.com/questions/2819696/parsing-properties-file-in-python/2819788#2819788
    """
    FAKE_SECTION = "dummySection"
    def __init__(self, fp):
        self.fp = fp
        self.sechead = '[' + self.FAKE_SECTION + ']\n'
    def readline(self):
        if self.sechead:
            try: return self.sechead
            finally: self.sechead = None
        else: return self.fp.readline()


def prepend_newest_version_changelog(projectIdentity):
    ChangelogWriter().prepend_version(ChangelogGenerator(projectIdentity)
                                      .generate_last_version_changelog(False))

def prepend_specific_version_changelog(projectIdentity, versionId):
    ChangelogWriter().prepend_version(ChangelogGenerator(projectIdentity)
                                      .generate_specific_version_changelog(versionId, False))

if __name__ == "__main__":
    if (len(sys.argv) < 2):
        raise Exception("Please specify a project name or ID")
    if (len(sys.argv) < 3):
        prepend_newest_version_changelog(sys.argv[1])
    else:
        prepend_specific_version_changelog(sys.argv[1], sys.argv[2])
