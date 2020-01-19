package hu.galambo.gobelin.views;

import org.gobelinmaker.gobelinmaker.devicemanager.IDevicaCommandCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Push;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import hu.galambo.gobelin.device.DeviceManager;

@Push
@SuppressWarnings("serial")
public class DeviceSerialTerminalView extends DeviceSerialTerminalDesign implements View {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(DeviceSerialTerminalView.class);

	public DeviceSerialTerminalView() {
		init();
	}

	private void init() {
		
		JsonObject json = Json.createObject();
		
		JsonArray address = Json.createArray();
		address.set(address.length(), 2);
		address.set(address.length(), 4);
		address.set(address.length(), 6);
		json.put("a", address);
		
		json.put("b", Json.create("e"));
		
		getCommandTextField().setValue(json.toJson());

		Runnable run = () -> {

			String commandToSend = getCommandTextField().getValue();

			if (commandToSend == null || commandToSend.isEmpty()) {
				return;
			}

			getSendCommandButton().setEnabled(false);

			DeviceManager.getInstance().getFirst().sendCommandAsync(commandToSend, new IDevicaCommandCallback() {
				@Override
				public void onSuccess(String response) {
					getUI().access(() -> appendResponse(response));
				}

				@Override
				public void onError(Exception e) {
					getUI().access(() -> appendResponse(e.getLocalizedMessage()));
				}

				@Override
				public void onAlways() {
					getUI().access(() -> {
						getSendCommandButton().setEnabled(true);

						getCommandTextField().focus();
						getCommandTextField().selectAll();
					});
					
				}
			});

		};

		getSendCommandButton().addClickListener(event -> run.run());

		getCommandTextField()
				.addShortcutListener(new ShortcutListener("Send command", ShortcutAction.KeyCode.ENTER, null) {
					@Override
					public void handleAction(Object sender, Object target) {
						run.run();
					}
				});

		getCommandTextField().focus();
	}

	private void appendResponse(String response) {
		String history = getResponseTextArea().getValue();
		if (!history.isEmpty()) {
			history += "\n";
		}

		getResponseTextArea().setValue(history + response);
	}

}
