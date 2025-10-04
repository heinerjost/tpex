/* Copyright 2025 Heiner Jostkleigrewe

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package de.jostnet.tpex;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lombok.Getter;

import org.apache.commons.cli.*;

public class CliOptions {

    @Getter
    private String zip;

    @Getter
    private String input;

    @Getter
    private String output;

    @Getter
    private String folderstoskip;

    @Getter
    private String cmd;

    public CliOptions(String[] args) {
        Options options = new Options();

        Option cmd = new Option("c", "command", true, "Kommando: analyze");
        options.addOption(cmd);

        Option zip = new Option("z", "zip", true, "Pfad zum Zip-Verzeichnis");
        zip.setRequired(true);
        options.addOption(zip);

        Option input = new Option("i", "input", true, "Pfad zum Takeout-Verzeichnis");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "Pfad zum Ausgabe-Verzeichnis");
        output.setRequired(true);
        options.addOption(output);

        Option folderstoskip = new Option("s", "skipfolders", true,
                "Datei, in der die zu überspringen Ordner zeilenweise eingetragen sind");
        options.addOption(folderstoskip);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmdline = parser.parse(options, args);
            if (cmdline.hasOption("c")) {
                this.cmd = cmdline.getOptionValue("c");
            }
            if (!cmdline.hasOption("c")) {
                this.cmd = "all";
            }
            if (cmdline.hasOption("z")) {
                this.zip = removeQuotationsMarks(cmdline.getOptionValue("z"));
            }
            if (cmdline.hasOption("i")) {
                this.input = removeQuotationsMarks(cmdline.getOptionValue("i"));
            }
            if (cmdline.hasOption("o")) {
                this.output = removeQuotationsMarks(cmdline.getOptionValue("o"));
            }
            if (cmdline.hasOption("s")) {
                this.folderstoskip = removeQuotationsMarks(cmdline.getOptionValue("s"));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar tpex-*.jar ", options);
            System.exit(1);
        }
    }

    private String removeQuotationsMarks(String s) {
        if (s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

}