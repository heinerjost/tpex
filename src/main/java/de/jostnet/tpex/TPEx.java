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

import java.util.Locale;

import de.jostnet.tpex.gui.Gui;
import de.jostnet.tpex.services.ExportService;
import de.jostnet.tpex.services.MessageService;
import de.jostnet.tpex.services.UnzipService;

public class TPEx {

	private Gui gui;

	private ExportService exportService;

	private MessageService messageService;

	private UnzipService unzipService;

	public static void main(String[] args) {
		new TPEx();
	}

	public TPEx() {
		messageService = new MessageService();
		messageService.setLocale(Locale.getDefault().getCountry());
		unzipService = new UnzipService();
		unzipService.setMessageService(messageService);
		exportService = new ExportService();
		exportService.setMessageService(messageService);
		gui = new Gui(messageService, unzipService, exportService);
		gui.open();
	}
}
