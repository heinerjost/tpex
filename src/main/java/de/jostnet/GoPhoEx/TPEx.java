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

package de.jostnet.GoPhoEx;

import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Getter;
import lombok.Setter;

@SpringBootApplication
public class TPEx implements CommandLineRunner {

	@Autowired
	private ExportService exportService;

	@Autowired
	private UnzipService unzipService;

	@Getter
	@Setter
	private boolean skipvideos = true;

	public static void main(String[] args) {
		SpringApplication.run(TPEx.class, args);
	}

	@Override
	public void run(String... args) throws IOException, ParseException,
			ImageReadException, ImageWriteException {
		CliOptions options = new CliOptions(args);
		go(options);
	}

	public void go(CliOptions options) throws ParseException, IOException, ImageReadException, ImageWriteException {

		if (options.getCmd().equals("unzip")) {
			unzipService.extractAllZips(options);
			return;
		}
		if (options.getCmd().equals("export")) {
			exportService.export(options);
			return;
		}

	}

}
